package com.henrique.nookio_api.infraestructure.microsservices.payment;

import com.henrique.nookio_api.infraestructure.microsservices.payment.dto.PaymentWebhookDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/payments/webhook")
@RequiredArgsConstructor
public class PaymentWebhookController {

    private final PaymentsPort paymentsPort;

    @PatchMapping
    public ResponseEntity<Void> handlePaymentWebhook(@RequestBody PaymentWebhookDto webhook) {
        log.info("[PAYMENT_WEBHOOK_RECEIVED] scheduleId={} paymentId={} status={}",
                webhook.scheduleId(), webhook.paymentId(), webhook.status());
        paymentsPort.responsePayment(webhook);
        return ResponseEntity.ok().build();
    }
}
