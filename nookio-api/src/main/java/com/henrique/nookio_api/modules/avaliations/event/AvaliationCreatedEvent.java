package com.henrique.nookio_api.modules.avaliations.event;

import java.math.BigDecimal;

public record AvaliationCreatedEvent(
        Integer ownerId,
        Integer propertyId,
        Integer avaliationId,
        Integer avaliatorId,
        BigDecimal rating,
        String description
) {
}
