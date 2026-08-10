package com.henrique.nookio_payments.services;

import com.henrique.nookio_payments.client.NookioApiFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentWebhookDispatcher {

    private final NookioApiFeignClient nookioApiFeignClient;

    @Async
    public void dispatchWebhookAsync(Integer scheduleId, String paymentId, String status) {
        try {
            log.info("[PAYMENTS_WEBHOOK_DISPATCHING] scheduleId={} paymentId={} status={}", scheduleId, paymentId, status);
            nookioApiFeignClient.sendPaymentStatusWebhook(Map.of(
                    "schedule_id", scheduleId,
                    "payment_id", paymentId,
                    "status", status
            ));
            log.info("[PAYMENTS_WEBHOOK_DISPATCHED] scheduleId={} paymentId={}", scheduleId, paymentId);
        } catch (Exception e) {
            log.warn("[PAYMENTS_WEBHOOK_FAILED] scheduleId={} error={}", scheduleId, e.getMessage());
        }
    }
}
