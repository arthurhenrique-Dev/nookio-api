package com.henrique.nookio_api.modules.schedules.dto;

import jakarta.validation.Valid;

public record PaymentRequestDto(

        @Valid CustomerDetailsInfo customerDetailsInfo,
        PaymentDetailsInfo paymentDetailsInfo
        ) {
}
