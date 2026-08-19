package com.henrique.nookio_api.infraestructure.microsservices.payment.dto;

import java.util.UUID;

public record PaymentResponseDto(
        UUID paymentId,
        String status,
        String message
) {
}
