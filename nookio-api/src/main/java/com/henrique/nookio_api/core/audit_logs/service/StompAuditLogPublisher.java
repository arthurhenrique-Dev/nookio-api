package com.henrique.nookio_api.core.audit_logs.service;

import com.henrique.nookio_api.core.audit_logs.model.AuditLogData;
import com.henrique.nookio_api.core.audit_logs.model.AuditLogEntity;
import com.henrique.nookio_api.core.audit_logs.repository.AuditLogFallbackRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StompAuditLogPublisher {

    private final AuditLogFallbackRepository fallbackRepository;
    private final DiscoveryClient discoveryClient;

    @Value("${clients.api-id:1}")
    private Integer apiId;

    private StompSession session;

    private String getAnalyticsWsUrl() {
        List<ServiceInstance> instances = discoveryClient.getInstances("nookio-analytics-api");
        if (instances == null || instances.isEmpty()) {
            instances = discoveryClient.getInstances("NOOKIO-ANALYTICS-API");
        }
        if (instances == null || instances.isEmpty()) {
            throw new IllegalStateException("No instances of nookio-analytics-api found in Eureka!");
        }
        ServiceInstance instance = instances.get(0);
        String url = String.format("ws://%s:%d/ws-analytics", instance.getHost(), instance.getPort());
        log.info("[EUREKA_DISCOVERY_SUCCESS] Discovered nookio-analytics-api WebSocket URL: {}", url);
        return url;
    }

    private synchronized StompSession getOrConnectSession() throws Exception {
        if (session != null && session.isConnected()) {
            return session;
        }
        String url = getAnalyticsWsUrl();
        log.info("[STOMP_CONNECTING] Connecting to nookio-analytics-api WebSocket at {}...", url);
        List<Transport> transports = Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()));
        SockJsClient sockJsClient = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        session = stompClient.connectAsync(url, new StompSessionHandlerAdapter() {
            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                log.error("[STOMP_TRANSPORT_ERROR] Transport error occurred", exception);
            }
        }).get();
        log.info("[STOMP_CONNECTED] Successfully connected to WebSocket!");
        return session;
    }

    @CircuitBreaker(name = "analyticsStompService", fallbackMethod = "fallbackSaveLocal")
    public void sendAuditLog(AuditLogData data) {
        try {
            StompSession activeSession = getOrConnectSession();
            Map<String, Object> logItem = new HashMap<>();
            logItem.put("ip", data.getIp());
            logItem.put("resource", data.getResource());
            logItem.put("operation", data.getOperation());
            logItem.put("result", data.getResult());
            logItem.put("timestamp", data.getTimestamp() != null ? data.getTimestamp().toString() : LocalDateTime.now().toString());

            Map<String, Object> payload = new HashMap<>();
            payload.put("api_id", apiId);
            payload.put("logs", List.of(logItem));

            activeSession.send("/app/analytic-logs", payload);
            log.info("[STOMP_SENT_SUCCESS] Audit log sent to /app/analytic-logs");
        } catch (Exception e) {
            disconnectSession();
            log.error("[STOMP_SEND_FAILED] Failed to send via STOMP WebSocket", e);
            throw new RuntimeException("STOMP WebSocket send failure", e);
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
