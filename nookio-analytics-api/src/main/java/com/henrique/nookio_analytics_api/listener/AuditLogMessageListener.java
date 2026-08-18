package com.henrique.nookio_analytics_api.listener;

import com.henrique.nookio_analytics_api.config.RabbitMQConfig;
import com.henrique.nookio_analytics_api.dto.AuditLogRequestDto;
import com.henrique.nookio_analytics_api.services.AnalyticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditLogMessageListener {

    private final AnalyticService analyticService;

    @RabbitListener(queues = RabbitMQConfig.AUDIT_LOGS_QUEUE)
    public void receiveAuditLogMessage(AuditLogRequestDto payload) {
        log.info("[RABBITMQ_AUDIT_LOG_RECEIVED] ApiId: {}", payload.apiId());
        analyticService.saveAuditLogs(payload);
    }
}
