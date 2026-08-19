package com.henrique.nookio_analytics_api.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String AUDIT_LOGS_TOPIC = "audit-logs";

    @Bean
    public NewTopic auditLogsTopic() {
        return TopicBuilder.name(AUDIT_LOGS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
