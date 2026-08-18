package com.henrique.nookio_api.infraestructure.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "nookio.direct.exchange";
    public static final String AUDIT_LOGS_ROUTING_KEY = "audit.logs";
    public static final String EMAIL_SEND_ROUTING_KEY = "email.send";
    public static final String PAYMENT_WEBHOOK_QUEUE = "payment.webhook.queue";
    public static final String PAYMENT_WEBHOOK_ROUTING_KEY = "payment.webhook";

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue paymentWebhookQueue() {
        return new Queue(PAYMENT_WEBHOOK_QUEUE, true);
    }

    @Bean
    public Binding paymentWebhookBinding(Queue paymentWebhookQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(paymentWebhookQueue).to(directExchange).with(PAYMENT_WEBHOOK_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
