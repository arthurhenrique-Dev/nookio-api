package com.henrique.nookio_api.infraestructure.messaging;

import com.henrique.nookio_api.infraestructure.microsservices.payment.PaymentsPort;
import com.henrique.nookio_api.infraestructure.microsservices.payment.dto.PaymentWebhookDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentStatusListener {

    private final PaymentsPort paymentsPort;

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_WEBHOOK_QUEUE)
    public void handlePaymentStatusWebhook(PaymentWebhookDto webhook) {
        log.info("[RABBITMQ_PAYMENT_WEBHOOK_RECEIVED] scheduleId={} paymentId={} status={}",
                webhook.scheduleId(), webhook.paymentId(), webhook.status());
        paymentsPort.responsePayment(webhook);
    }
}
