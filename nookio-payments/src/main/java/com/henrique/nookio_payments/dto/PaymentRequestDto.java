package com.henrique.nookio_payments.dto;

import java.math.BigDecimal;

public record PaymentRequestDto(
        Integer scheduleId,
        BigDecimal amount,
        String paymentMethodToken
) {
}
