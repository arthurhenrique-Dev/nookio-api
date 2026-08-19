package com.henrique.nookio_api.modules.properties.services.analytics;

import com.henrique.nookio_api.core.audit_logs.model.AuditLogData;
import com.henrique.nookio_api.core.health_monitor.ApplicationStress;
import com.henrique.nookio_api.infraestructure.microsservices.analytic.AnalyticsPort;
import com.henrique.nookio_api.modules.properties.models.SearchLogEntity;
import com.henrique.nookio_api.modules.properties.repository.SearchLogFallbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchLogRetryService {

    private static final int BATCH_CHUNK_SIZE = 120;

    private final SearchLogFallbackRepository fallbackRepository;
    private final AnalyticsPort analyticsPort;
    private final ApplicationStress stress;

    public void processPendingLogsInBatch() {
        boolean shouldContinue = true;

        while (shouldContinue && !stress.isStressed()) {
            List<SearchLogEntity> pendingEntities = fallbackRepository
                    .findAllByOrderByTimestampAsc(PageRequest.of(0, BATCH_CHUNK_SIZE))
                    .getContent();

            if (pendingEntities.isEmpty()) shouldContinue = false;
            else shouldContinue = processBatchChunk(pendingEntities);
        }
    }

    private boolean processBatchChunk(List<SearchLogEntity> pendingEntities) {
        List<AuditLogData> dataList = pendingEntities.stream()
                .map(entity -> AuditLogData.builder()
                        .timestamp(entity.getTimestamp())
                        .resource("CATALOG_SEARCH")
                        .operation(entity.getCanonicalParams())
                        .result(200)
                        .build())
                .toList();

        log.info("Sending batch chunk of {} pending search logs to Analytics...", dataList.size());

        try {
            boolean isAccepted = analyticsPort.sendAuditLogs(dataList);

            if (isAccepted) {
                fallbackRepository.truncateTable();
                log.info("Successfully dispatched chunk of {} search logs and purged fallback repository.", dataList.size());
                return true;
            }

            log.warn("Analytics service responded with non-2xx status. Retaining chunk of {} search logs for next retry.", dataList.size());
            return false;
        } catch (Exception e) {
            log.error("Failed to dispatch search log chunk to Analytics. Retaining logs for next retry cycle.", e);
            return false;
        }
    }
}
