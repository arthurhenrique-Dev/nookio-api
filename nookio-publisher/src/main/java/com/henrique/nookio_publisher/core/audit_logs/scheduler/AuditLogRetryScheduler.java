package com.henrique.nookio_publisher.core.audit_logs.scheduler;

import com.henrique.nookio_publisher.core.audit_logs.client.AnalyticsFeignClient;
import com.henrique.nookio_publisher.core.audit_logs.model.AuditLogFallback;
import com.henrique.nookio_publisher.core.audit_logs.repository.AuditLogFallbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditLogRetryScheduler {

    private final AuditLogFallbackRepository fallbackRepository;
    private final AnalyticsFeignClient analyticsFeignClient;

    @Value("${clients.api-id:2}")
    private Integer apiId;

    @Scheduled(fixedDelay = 60000)
    public void retryLocalAuditLogs() {
        List<AuditLogFallback> fallbacks = fallbackRepository.findAll();
        if (fallbacks.isEmpty()) return;

        log.info("[PUBLISHER_AUDIT_LOG_RETRY_STARTED] retrying {} local audit logs", fallbacks.size());

        List<Map<String, Object>> payloadLogs = fallbacks.stream()
                .map(item -> Map.<String, Object>of(
                        "ip", item.getIp() != null ? item.getIp() : "127.0.0.1",
                        "resource", item.getResource(),
                        "operation", item.getOperation(),
                        "result", item.getResult(),
                        "timestamp", item.getTimestamp().toString()
                ))
                .toList();

        try {
            analyticsFeignClient.sendAuditLogs(Map.of(
                    "api_id", apiId,
                    "logs", payloadLogs
            ));
            fallbackRepository.deleteAll(fallbacks);
            log.info("[PUBLISHER_AUDIT_LOG_RETRY_SUCCESS] cleared {} local audit logs", fallbacks.size());
        } catch (Exception e) {
            log.warn("[PUBLISHER_AUDIT_LOG_RETRY_FAILED] analytics-api still unreachable: {}", e.getMessage());
        }
    }
}
