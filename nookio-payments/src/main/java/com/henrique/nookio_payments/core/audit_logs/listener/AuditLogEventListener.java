package com.henrique.nookio_payments.core.audit_logs.listener;

import com.henrique.nookio_payments.core.audit_logs.client.AnalyticsFeignClient;
import com.henrique.nookio_payments.core.audit_logs.event.AuditLogEvent;
import com.henrique.nookio_payments.core.audit_logs.model.AuditLogFallback;
import com.henrique.nookio_payments.core.audit_logs.repository.AuditLogFallbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditLogEventListener {

    private final AnalyticsFeignClient analyticsFeignClient;
    private final AuditLogFallbackRepository fallbackRepository;

    @Value("${clients.api-id:3}")
    private Integer apiId;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAuditLogEvent(AuditLogEvent event) {
        if (event == null) return;

        Map<String, Object> logItem = Map.of(
                "ip", event.ip() != null ? event.ip() : "127.0.0.1",
                "resource", event.resource(),
                "operation", event.operation(),
                "result", event.result(),
                "timestamp", event.timestamp().toString()
        );

        try {
            analyticsFeignClient.sendAuditLogs(Map.of(
                    "api_id", apiId,
                    "logs", List.of(logItem)
            ));
            log.info("[PAYMENTS_AUDIT_LOG_SENT] apiId={} resource={} operation={}", apiId, event.resource(), event.operation());
        } catch (Exception e) {
            log.warn("[PAYMENTS_AUDIT_LOG_FALLBACK] Saving audit log locally in SQLite. Error: {}", e.getMessage());
            AuditLogFallback fallback = AuditLogFallback.builder()
                    .ip(event.ip())
                    .resource(event.resource())
                    .operation(event.operation())
                    .result(event.result())
                    .timestamp(event.timestamp())
                    .build();
            fallbackRepository.save(fallback);
        }
    }
}
