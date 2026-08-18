package com.henrique.nookio_api.infraestructure.messaging;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String AUDIT_LOGS_TOPIC = "audit-logs";
    public static final String EMAIL_SEND_TOPIC = "email-send";
    public static final String PAYMENT_WEBHOOK_TOPIC = "payment-webhook";

    @Bean
    public NewTopic paymentWebhookTopic() {
        return TopicBuilder.name(PAYMENT_WEBHOOK_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
