package com.henrique.nookio_publisher.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String EMAIL_SEND_TOPIC = "email-send";
    public static final String AUDIT_LOGS_TOPIC = "audit-logs";

    @Bean
    public NewTopic emailSendTopic() {
        return TopicBuilder.name(EMAIL_SEND_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
