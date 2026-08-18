package com.henrique.nookio_api.core.audit_logs.service;

import com.henrique.nookio_api.core.audit_logs.model.AuditLogData;
import com.henrique.nookio_api.core.audit_logs.model.AuditLogEntity;
import com.henrique.nookio_api.core.audit_logs.repository.AuditLogFallbackRepository;
import com.henrique.nookio_api.infraestructure.messaging.RabbitMQConfig;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StompAuditLogPublisher {

    private final AuditLogFallbackRepository fallbackRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${clients.api-id:1}")
    private Integer apiId;

    @CircuitBreaker(name = "analyticsStompService", fallbackMethod = "fallbackSaveLocal")
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

            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.AUDIT_LOGS_ROUTING_KEY, payload);
            log.info("[RABBITMQ_SENT_SUCCESS] Audit log sent to exchange {} routing key {}", RabbitMQConfig.EXCHANGE, RabbitMQConfig.AUDIT_LOGS_ROUTING_KEY);
        } catch (Exception e) {
            log.error("[RABBITMQ_SEND_FAILED] Failed to send audit log via RabbitMQ", e);
            throw new RuntimeException("RabbitMQ send failure", e);
        }
    }

    public void fallbackSaveLocal(AuditLogData data, Throwable t) {
        log.warn("[STOMP_FALLBACK_TRIGGERED] Circuit Breaker OPEN or WebSocket failure. Saving log to management.local_logs DB table");
        disconnectSession();
        try {
            if (data.getTimestamp() == null) {
                data.setTimestamp(LocalDateTime.now());
            }
            AuditLogEntity entity = AuditLogEntity.builder()
                    .auditLogData(data)
                    .build();
            fallbackRepository.save(entity);
        } catch (Exception ex) {
            log.error("[STOMP_FALLBACK_ERROR] Failed to save local audit log to DB", ex);
        }
    }

    public synchronized void disconnectSession() {
        if (session != null) {
            try {
                session.disconnect();
            } catch (Exception e) {
                log.debug("Session disconnect exception", e);
            }
            session = null;
        }
    }
}
