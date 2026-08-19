package com.henrique.nookio_api.modules.properties.interfaces;

import com.henrique.nookio_api.modules.properties.dto.CatalogationParameters;
import com.henrique.nookio_api.modules.properties.dto.InputCatalog;
import com.henrique.nookio_api.shared.input.Range;
import com.henrique.nookio_api.shared.input.RangeInput;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CatalogMapper {

    @Mapping(target = "propertyTypes", source = "info.propertyTypes")
    @Mapping(target = "bedrooms", source = "info.bedrooms")
    @Mapping(target = "locationInput", source = "info.locationInput")
    @Mapping(target = "bathrooms", source = "info.bathrooms")
    @Mapping(target = "beds", source = "info.beds")
    @Mapping(target = "maxGuests", source = "info.maxGuests")
    @Mapping(target = "parkingSpaces", source = "info.parkingSpaces")
    @Mapping(target = "areaSqm", source = "info.areaSqm")
    @Mapping(target = "pools", source = "info.pools")
    @Mapping(target = "nextToBeach", source = "info.nextToBeach")
    @Mapping(target = "petFriendly", source = "info.petFriendly")
    @Mapping(target = "hasWifi", source = "info.hasWifi")
    @Mapping(target = "hasAirConditioning", source = "info.hasAirConditioning")
    @Mapping(target = "favorableSeason", source = "info.favorableSeason")
    @Mapping(target = "price", source = "info.price")
    @Mapping(target = "cleaningFee", source = "info.cleaningFee")
    CatalogationParameters toParameters(InputCatalog input);

    default <T extends Comparable<? super T>> Range<T> toRange(RangeInput<T> rangeInput) {
        if (rangeInput == null) {
            return null;
        }
        return Range.of(rangeInput.min(), rangeInput.max());
    }
}
