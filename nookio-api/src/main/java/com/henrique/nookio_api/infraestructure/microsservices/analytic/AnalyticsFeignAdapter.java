package com.henrique.nookio_api.infraestructure.microsservices.analytic;

import com.henrique.nookio_api.core.audit_logs.model.AuditLogData;
import com.henrique.nookio_api.infraestructure.messaging.KafkaConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Component
public class AnalyticsFeignAdapter implements AnalyticsPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Integer apiId;

    public AnalyticsFeignAdapter(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${clients.api-id}") Integer apiId
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.apiId = apiId;
    }

    @Override
    public boolean sendAuditLogs(Collection<AuditLogData> dataList) {
        try {
            kafkaTemplate.send(KafkaConfig.AUDIT_LOGS_TOPIC, Map.of(
                    "api_id", apiId,
                    "logs", dataList
            ));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
