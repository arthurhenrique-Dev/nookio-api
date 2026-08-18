package com.henrique.nookio_payments.services;

import com.henrique.nookio_payments.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentWebhookDispatcher {

    private final RabbitTemplate rabbitTemplate;

    @Async
    public void dispatchWebhookAsync(Integer scheduleId, String paymentId, String status) {
        try {
            log.info("[PAYMENTS_WEBHOOK_DISPATCHING] scheduleId={} paymentId={} status={}", scheduleId, paymentId, status);
            Map<String, Object> payload = Map.of(
                    "schedule_id", scheduleId,
                    "payment_id", paymentId != null ? paymentId : "",
                    "status", status
            );
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.PAYMENT_WEBHOOK_ROUTING_KEY, payload);
            log.info("[PAYMENTS_WEBHOOK_DISPATCHED] scheduleId={} paymentId={}", scheduleId, paymentId);
        } catch (Exception e) {
            log.warn("[PAYMENTS_WEBHOOK_FAILED] scheduleId={} error={}", scheduleId, e.getMessage());
        }
    }
}
