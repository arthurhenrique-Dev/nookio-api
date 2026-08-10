package com.henrique.nookio_api.infraestructure.microsservices.payment;

import com.henrique.nookio_api.infraestructure.microsservices.payment.dto.PaymentResponseDto;
import com.henrique.nookio_api.infraestructure.microsservices.payment.dto.PaymentWebhookDto;
import com.henrique.nookio_api.modules.schedules.dto.PaymentRequestDto;

import java.util.List;
import java.util.UUID;

public interface PaymentsPort {
    PaymentResponseDto sendPayment(PaymentRequestDto request);
    void responsePayment(PaymentWebhookDto webhook);
    void repay(List<UUID> payments);
}
