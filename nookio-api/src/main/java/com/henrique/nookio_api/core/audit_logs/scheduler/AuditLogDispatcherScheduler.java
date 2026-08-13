package com.henrique.nookio_api.core.audit_logs.scheduler;

import com.henrique.nookio_api.core.audit_logs.model.AuditLogEntity;
import com.henrique.nookio_api.core.audit_logs.repository.AuditLogFallbackRepository;
import com.henrique.nookio_api.core.audit_logs.service.StompAuditLogPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditLogDispatcherScheduler {

    private final AuditLogFallbackRepository fallbackRepository;
    private final StompAuditLogPublisher stompPublisher;

    @Scheduled(fixedDelay = 300000) // Runs every 5 minutes (300,000 ms)
    @Transactional
    public void dispatchPendingAuditLogs() {
        List<AuditLogEntity> pendingLogs = fallbackRepository.findAll();
        if (pendingLogs.isEmpty()) {
            return;
        }

        log.info("[DISPATCHER_SCHEDULER] Found {} pending local audit logs to dispatch.", pendingLogs.size());
        for (AuditLogEntity entity : pendingLogs) {
            try {
                stompPublisher.sendAuditLog(entity.getAuditLogData());
                fallbackRepository.delete(entity);
                log.info("[DISPATCHER_SUCCESS] Dispatched local audit log ID: {}", entity.getId());
            } catch (Exception e) {
                log.error("[DISPATCHER_FAILED] Failed to dispatch local audit log ID: {}. Will retry in 5 minutes.", entity.getId(), e);
                break; // Stop loop if connection is still down
            }
        }
    }
}
