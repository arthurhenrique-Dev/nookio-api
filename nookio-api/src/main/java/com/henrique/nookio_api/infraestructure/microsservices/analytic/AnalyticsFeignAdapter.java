package com.henrique.nookio_api.infraestructure.microsservices.analytic;

import com.henrique.nookio_api.core.audit_logs.model.AuditLogData;
import com.henrique.nookio_api.infraestructure.messaging.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Component
public class AnalyticsFeignAdapter implements AnalyticsPort {

    private final RabbitTemplate rabbitTemplate;
    private final Integer apiId;

    public AnalyticsFeignAdapter(
            RabbitTemplate rabbitTemplate,
            @Value("${clients.api-id}") Integer apiId
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.apiId = apiId;
    }

    @Override
    public boolean sendAuditLogs(Collection<AuditLogData> dataList) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.AUDIT_LOGS_ROUTING_KEY, Map.of(
                    "api_id", apiId,
                    "logs", dataList
            ));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
