package com.henrique.nookio_api.modules.properties.repository;

import com.henrique.nookio_api.modules.location.dto.LocationInput;
import com.henrique.nookio_api.modules.properties.dto.CatalogationParameters;
import com.henrique.nookio_api.modules.properties.models.VwPropertiesCatalog;
import com.henrique.nookio_api.shared.generic_specs.SpecHelper;
import com.henrique.nookio_api.shared.input.InputPreSet;
import com.henrique.nookio_api.shared.input.Range;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;

public class VwPropertiesCatalogSpecs {

    public static Specification<VwPropertiesCatalog> filteredCatalog(CatalogationParameters parameters) {
        if (parameters == null) return (root, query, cb) -> null;

        return Specification
                .where(bySearchTerm(parameters.getSearch()))
                .and(byInfo(parameters))
                .and(byLocation(parameters.getLocationInput()));
    }

    public static Pageable pageable(InputPreSet preSet) {
        if (preSet == null) return PageRequest.of(0, 20);
        return preSet.pageable();
    }

    private static Specification<VwPropertiesCatalog> bySearchTerm(String searchTerm) {
        return SpecHelper.searchSpec(
                searchTerm,
                List.of(
                        "title",
                        "ownerName",
                        "location.city",
                        "location.neighborhood",
                        "location.state"
                )
        );
    }

    private static Specification<VwPropertiesCatalog> byInfo(CatalogationParameters parameters) {
        if (parameters == null) return (root, query, cb) -> null;

        String path = "information.info.";

        return Specification.<VwPropertiesCatalog>where(
                SpecHelper.inSpec(parameters.getPropertyTypes(), path + "propertyType")
        )
        .and(threatRanges(parameters.getBedrooms(), path + "bedrooms", 0))
        .and(threatRanges(parameters.getBathrooms(), path + "bathrooms", 0))
        .and(threatRanges(parameters.getBeds(), path + "beds", 0))
        .and(threatRanges(parameters.getMaxGuests(), path + "maxGuests", 0))
        .and(threatRanges(parameters.getParkingSpaces(), path + "parkingSpaces", 0))
        .and(threatRanges(parameters.getAreaSqm(), path + "areaSqm", BigDecimal.ZERO))
        .and(threatRanges(parameters.getPools(), path + "pools", 0))
        .and(SpecHelper.eqSpec(parameters.getNextToBeach(), path + "nextToBeach"))
        .and(SpecHelper.eqSpec(parameters.getPetFriendly(), path + "petFriendly"))
        .and(SpecHelper.eqSpec(parameters.getHasWifi(), path + "hasWifi"))
        .and(SpecHelper.eqSpec(parameters.getHasAirConditioning(), path + "hasAirConditioning"))
        .and(SpecHelper.eqSpec(parameters.getFavorableSeason(), path + "favorableSeason"))
        .and(threatRanges(parameters.getPrice(), "totalPrice", BigDecimal.valueOf(40)));
    }

    private static Specification<VwPropertiesCatalog> byLocation(LocationInput location) {
        if (location == null) return (root, query, cb) -> null;

        String path = "location.";

        return Specification.<VwPropertiesCatalog>where(
                SpecHelper.eqSpec(location.city(), path + "city")
        )
        .and(SpecHelper.eqSpec(location.state(), path + "state"))
        .and(SpecHelper.eqSpec(location.neighborhood(), path + "neighborhood"))
        .and(SpecHelper.eqSpec(location.street(), path + "street"))
        .and(SpecHelper.eqSpec(location.zipCode(), path + "zipCode"))
        .and(SpecHelper.eqSpec(location.country(), path + "country"));
    }

    private static <T extends Comparable<? super T>> Specification<VwPropertiesCatalog> threatRanges(Range<T> range, String field, T defaultStart) {
        if (range == null) return null;

        T start = (range.start() != null) ? range.start() : defaultStart;
        T end = range.end();

        return SpecHelper.rangeSpec(start, end, field);
    }
}
