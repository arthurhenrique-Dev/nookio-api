package com.henrique.nookio_api.infraestructure.microsservices.payment.dto;

import java.util.UUID;

public record PaymentWebhookDto(
        Integer scheduleId,
        UUID paymentId,
        String status,
        String message
) {
}
