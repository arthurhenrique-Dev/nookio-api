package com.henrique.nookio_api.modules.properties.dto;

import com.henrique.nookio_api.modules.location.dto.LocationInput;
import com.henrique.nookio_api.modules.properties.models.PropertyType;
import com.henrique.nookio_api.modules.properties.models.Season;
import com.henrique.nookio_api.shared.input.InputPreSet;
import com.henrique.nookio_api.shared.input.Range;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CatalogationParameters{
        private InputPreSet inputPreSet;
        private String search;
        private LocationInput locationInput;
        private List<PropertyType> propertyTypes;
        private Range<Integer> bedrooms;
        private Range<Integer> bathrooms;
        private Range<Integer> beds;
        private Range<Integer> maxGuests;
        private Range<Integer> parkingSpaces;
        private Range<BigDecimal> areaSqm;
        private Range<Integer> pools;
        private Boolean nextToBeach;
        private Boolean petFriendly;
        private Boolean hasWifi;
        private Boolean hasAirConditioning;
        private Season favorableSeason;
        private Range<BigDecimal> price;
        private Range<BigDecimal> cleaningFee;
}
