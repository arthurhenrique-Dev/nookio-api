package com.henrique.nookio_publisher.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "nookio.direct.exchange";
    public static final String EMAIL_SEND_QUEUE = "email.send.queue";
    public static final String EMAIL_SEND_ROUTING_KEY = "email.send";
    public static final String AUDIT_LOGS_ROUTING_KEY = "audit.logs";

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue emailSendQueue() {
        return new Queue(EMAIL_SEND_QUEUE, true);
    }

    @Bean
    public Binding emailSendBinding(Queue emailSendQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(emailSendQueue).to(directExchange).with(EMAIL_SEND_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
