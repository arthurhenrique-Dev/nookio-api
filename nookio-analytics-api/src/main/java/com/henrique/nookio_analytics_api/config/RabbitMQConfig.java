package com.henrique.nookio_analytics_api.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "nookio.direct.exchange";
    public static final String AUDIT_LOGS_QUEUE = "audit.logs.queue";
    public static final String AUDIT_LOGS_ROUTING_KEY = "audit.logs";

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue auditLogsQueue() {
        return new Queue(AUDIT_LOGS_QUEUE, true);
    }

    @Bean
    public Binding auditLogsBinding(Queue auditLogsQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(auditLogsQueue).to(directExchange).with(AUDIT_LOGS_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
