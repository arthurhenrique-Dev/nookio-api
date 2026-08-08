package com.henrique.nookio_api.modules.properties.listeners;

import com.henrique.nookio_api.core.audit_logs.model.AuditLogData;
import com.henrique.nookio_api.core.health_monitor.ApplicationStress;
import com.henrique.nookio_api.infraestructure.microsservices.analytic.AnalyticsPort;
import com.henrique.nookio_api.modules.properties.events.CatalogSearchEvent;
import com.henrique.nookio_api.modules.properties.models.SearchLogEntity;
import com.henrique.nookio_api.modules.properties.repository.SearchLogFallbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyticsSearchEventListener {

    private final AnalyticsPort analyticsPort;
    private final SearchLogFallbackRepository fallbackRepository;
    private final ApplicationStress stress;

    @Async
    @EventListener
    public void handleCatalogSearchEvent(CatalogSearchEvent event) {
        if (event == null || event.canonicalParams() == null || event.canonicalParams().isBlank()) {
            return;
        }

        if (stress.isStressed()) {
            log.warn("Application under high memory stress. Saving search telemetry to local JPA fallback repository...");
            saveToFallback(event);
            return;
        }

        try {
            AuditLogData auditData = AuditLogData.builder()
                    .timestamp(event.timestamp())
                    .resource("CATALOG_SEARCH")
                    .operation(event.canonicalParams())
                    .result(200)
                    .build();

            boolean sent = analyticsPort.sendAuditLogs(auditData);
            if (!sent) {
                log.warn("Analytics service returned non-2xx status for search event. Saving to local fallback repository...");
                saveToFallback(event);
            }
        } catch (Exception e) {
            log.error("Failed to send search event to Analytics. Saving to local fallback repository...", e);
            saveToFallback(event);
        }
    }

    private void saveToFallback(CatalogSearchEvent event) {
        SearchLogEntity entity = SearchLogEntity.builder()
                .canonicalParams(event.canonicalParams())
                .timestamp(event.timestamp())
                .build();
        fallbackRepository.save(entity);
    }
}
