package com.henrique.nookio_api.modules.properties.dto;

import java.math.BigDecimal;

public record InformationUpdate(
         Integer bedrooms,
         Integer bathrooms,
         Integer beds,
         Integer maxGuests,
         Integer parkingSpaces,
         BigDecimal areaSqm,
         Integer pools,
         boolean hasWifi,
         boolean hasAirConditioning,
         BigDecimal price,
         BigDecimal cleaningFee
) {
}
