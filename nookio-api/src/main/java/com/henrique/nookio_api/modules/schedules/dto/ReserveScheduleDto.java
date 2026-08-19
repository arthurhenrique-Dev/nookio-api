package com.henrique.nookio_api.modules.schedules.dto;

import java.time.LocalDate;

public record ReserveScheduleDto(
        Integer userId,
        Integer propertyId,
        Integer ownerId,
        LocalDate start,
        LocalDate end,
        PaymentDetailsInfo paymentDto
) {
}
