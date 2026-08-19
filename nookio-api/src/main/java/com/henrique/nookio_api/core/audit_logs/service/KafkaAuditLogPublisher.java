package com.henrique.nookio_api.core.audit_logs.service;

import com.henrique.nookio_api.core.audit_logs.model.AuditLogData;
import com.henrique.nookio_api.core.audit_logs.model.AuditLogEntity;
import com.henrique.nookio_api.core.audit_logs.repository.AuditLogFallbackRepository;
import com.henrique.nookio_api.infraestructure.messaging.KafkaConfig;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaAuditLogPublisher {

    private final AuditLogFallbackRepository fallbackRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${clients.api-id:1}")
    private Integer apiId;

    @CircuitBreaker(name = "analyticsService", fallbackMethod = "fallbackSaveLocal")
    public void sendAuditLog(AuditLogData data) {
        try {
            Map<String, Object> logItem = new HashMap<>();
            logItem.put("ip", data.getIp());
            logItem.put("resource", data.getResource());
            logItem.put("operation", data.getOperation());
            logItem.put("result", data.getResult());
            logItem.put("timestamp", data.getTimestamp() != null ? data.getTimestamp().toString() : LocalDateTime.now().toString());

            Map<String, Object> payload = new HashMap<>();
            payload.put("api_id", apiId);
            payload.put("logs", List.of(logItem));

            kafkaTemplate.send(KafkaConfig.AUDIT_LOGS_TOPIC, payload);
            log.info("[KAFKA_AUDIT_LOG_SENT] Sent audit log to Kafka topic {}", KafkaConfig.AUDIT_LOGS_TOPIC);
        } catch (Exception e) {
            log.error("[KAFKA_AUDIT_LOG_FAILED] Failed to send audit log via Kafka", e);
            throw new RuntimeException("Kafka send failure", e);
        }
    }

    public void fallbackSaveLocal(AuditLogData data, Throwable t) {
        log.warn("[KAFKA_FALLBACK_TRIGGERED] Saving log locally to DB table. Error: {}", t.getMessage());
        try {
            if (data.getTimestamp() == null) {
                data.setTimestamp(LocalDateTime.now());
            }
            AuditLogEntity entity = AuditLogEntity.builder()
                    .auditLogData(data)
                    .build();
            fallbackRepository.save(entity);
        } catch (Exception ex) {
            log.error("[KAFKA_FALLBACK_ERROR] Failed to save local audit log to DB", ex);
        }
    }
}
