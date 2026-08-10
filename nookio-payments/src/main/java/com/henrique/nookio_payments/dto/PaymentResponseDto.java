package com.henrique.nookio_payments.dto;

import java.math.BigDecimal;

public record PaymentResponseDto(
        String paymentId,
        Integer scheduleId,
        BigDecimal amount,
        String status
) {
}
