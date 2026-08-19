package com.henrique.nookio_payments.services;

import com.henrique.nookio_payments.dto.PaymentRequestDto;
import com.henrique.nookio_payments.dto.PaymentResponseDto;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripePaymentService {

    private final PaymentWebhookDispatcher paymentWebhookDispatcher;

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    public PaymentResponseDto processPayment(PaymentRequestDto dto) {
        Integer scheduleId = dto.scheduleId();
        BigDecimal amount = dto.amount();
        log.info("[STRIPE_PAYMENT_PROCESSING] scheduleId={} amount={}", scheduleId, amount);

        Stripe.apiKey = stripeSecretKey;
        long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValue();

        String paymentId;
        String status;

        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency("brl")
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build()
                    )
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);
            paymentId = intent.getId();
            status = intent.getStatus().toUpperCase();
            log.info("[STRIPE_PAYMENT_INTENT_CREATED] intentId={} status={}", paymentId, status);
        } catch (Exception e) {
            log.error("[STRIPE_PAYMENT_ERROR] scheduleId={} error={}", scheduleId, e.getMessage());
            paymentId = "err_stripe_" + scheduleId;
            status = "FAILED";
        }

        paymentWebhookDispatcher.dispatchWebhookAsync(scheduleId, paymentId, status);

        return new PaymentResponseDto(paymentId, scheduleId, amount, status);
    }
}
