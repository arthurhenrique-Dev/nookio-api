package com.henrique.nookio_api.core.audit_logs.service;

import com.henrique.nookio_api.core.audit_logs.model.AuditLogData;
import com.henrique.nookio_api.core.audit_logs.model.AuditLogEntity;
import com.henrique.nookio_api.infraestructure.microsservices.analytic.AnalyticsPort;
import com.henrique.nookio_api.core.audit_logs.repository.AuditLogFallbackRepository;
import com.henrique.nookio_api.core.health_monitor.ApplicationStress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogRetryScheduler {

    private static final int BATCH_CHUNK_SIZE = 80;

    private final AuditLogFallbackRepository fallbackRepository;
    private final AnalyticsPort analyticsPort;
    private final ApplicationStress stress;

    @Scheduled(fixedDelay = 15000)
    public void processPendingLogsInBatch() {
        boolean shouldContinue = true;

        while (shouldContinue && !stress.isStressed()) {
            List<AuditLogEntity> pendingEntities = fallbackRepository
                    .findAllByOrderByAuditLogDataTimestampAsc(PageRequest.of(0, BATCH_CHUNK_SIZE))
                    .getContent();

            if (pendingEntities.isEmpty()) {
                shouldContinue = false;
            } else {
                shouldContinue = processBatchChunk(pendingEntities);
            }
        }
    }

    private boolean processBatchChunk(List<AuditLogEntity> pendingEntities) {
        List<AuditLogData> dataList = pendingEntities.stream()
                .map(AuditLogEntity::getAuditLogData)
                .toList();

        log.info("Sending batch chunk of {} pending audit logs to Analytics...", dataList.size());

        try {
            boolean isAccepted = analyticsPort.sendAuditLogs(dataList);

            if (isAccepted) {
                fallbackRepository.truncate();
                log.info("Successfully dispatched chunk of {} logs and purged from local database.", dataList.size());
                return true;
            }

            log.warn("Analytics service responded with non-2xx status. Retaining chunk of {} logs in database for next retry.", dataList.size());
            return false;
        } catch (Exception e) {
            log.error("Failed to dispatch chunk to Analytics. Retaining logs in local database for next retry cycle.", e);
            return false;
        }
    }
}
