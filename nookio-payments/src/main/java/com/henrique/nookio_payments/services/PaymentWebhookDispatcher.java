package com.henrique.nookio_payments.services;

import com.henrique.nookio_payments.config.KafkaConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentWebhookDispatcher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Async
    public void dispatchWebhookAsync(Integer scheduleId, String paymentId, String status) {
        try {
            log.info("[PAYMENTS_WEBHOOK_DISPATCHING] scheduleId={} paymentId={} status={}", scheduleId, paymentId, status);
            Map<String, Object> payload = Map.of(
                    "schedule_id", scheduleId,
                    "payment_id", paymentId != null ? paymentId : "",
                    "status", status
            );
            kafkaTemplate.send(KafkaConfig.PAYMENT_WEBHOOK_TOPIC, payload);
            log.info("[PAYMENTS_WEBHOOK_DISPATCHED] scheduleId={} paymentId={}", scheduleId, paymentId);
        } catch (Exception e) {
            log.warn("[PAYMENTS_WEBHOOK_FAILED] scheduleId={} error={}", scheduleId, e.getMessage());
        }
    }
}
