package com.henrique.nookio_api.modules.properties.dto;

import com.henrique.nookio_api.modules.location.dto.LocationInput;
import com.henrique.nookio_api.modules.properties.models.PropertyType;
import com.henrique.nookio_api.modules.properties.models.Season;
import com.henrique.nookio_api.shared.input.RangeInput;

import java.math.BigDecimal;
import java.util.List;

public record CatalogInfoInput(
        List<PropertyType> propertyTypes,
        RangeInput<Integer> bedrooms,
        RangeInput<Integer> bathrooms,
        RangeInput<Integer> beds,
        RangeInput<Integer> maxGuests,
        RangeInput<Integer> parkingSpaces,
        RangeInput<BigDecimal> areaSqm,
        RangeInput<Integer> pools,
        Boolean nextToBeach,
        Boolean petFriendly,
        Boolean hasWifi,
        Boolean hasAirConditioning,
        Season favorableSeason,
        RangeInput<BigDecimal> price,
        RangeInput<BigDecimal> cleaningFee,
        LocationInput locationInput
) {
}
