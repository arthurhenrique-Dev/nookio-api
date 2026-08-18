package com.henrique.nookio_analytics_api.listener;

import com.henrique.nookio_analytics_api.config.KafkaConfig;
import com.henrique.nookio_analytics_api.dto.AuditLogRequestDto;
import com.henrique.nookio_analytics_api.services.AnalyticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditLogMessageListener {

    private final AnalyticService analyticService;

    @KafkaListener(topics = KafkaConfig.AUDIT_LOGS_TOPIC, groupId = "nookio-analytics-group")
    public void receiveAuditLogMessage(AuditLogRequestDto payload) {
        log.info("[KAFKA_AUDIT_LOG_RECEIVED] ApiId: {}", payload.apiId());
        analyticService.saveAuditLogs(payload);
    }
}
