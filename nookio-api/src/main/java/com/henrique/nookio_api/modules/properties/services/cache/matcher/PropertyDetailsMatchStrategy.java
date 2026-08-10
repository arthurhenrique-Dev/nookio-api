package com.henrique.nookio_api.modules.properties.services.cache.matcher;

import com.henrique.nookio_api.modules.properties.dto.CatalogInfoInput;
import com.henrique.nookio_api.modules.properties.dto.InputCatalog;
import com.henrique.nookio_api.modules.properties.models.PropertyInformation;
import com.henrique.nookio_api.modules.properties.models.PropertyInformationDetails;
import com.henrique.nookio_api.modules.properties.models.VwPropertiesCatalog;
import com.henrique.nookio_api.shared.input.RangeInput;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PropertyDetailsMatchStrategy implements CatalogCacheMatchStrategy {

    @Override
    public boolean matches(InputCatalog search, VwPropertiesCatalog property) {
        if (search == null || search.info() == null) return true;

        PropertyInformation infoEntity = property.getInformation();
        PropertyInformationDetails details = infoEntity != null ? infoEntity.getPropertyInformationDetails() : null;
        if (details == null) return true;

        CatalogInfoInput info = search.info();

        if (info.propertyTypes() != null && !info.propertyTypes().isEmpty() && !info.propertyTypes().contains(details.getPropertyType())) return false;

        if (!matchesIntRange(info.bedrooms(), details.getBedrooms()) ||
            !matchesIntRange(info.bathrooms(), details.getBathrooms()) ||
            !matchesIntRange(info.beds(), details.getBeds()) ||
            !matchesIntRange(info.maxGuests(), details.getMaxGuests()) ||
            !matchesIntRange(info.parkingSpaces(), details.getParkingSpaces()) ||
            !matchesIntRange(info.pools(), details.getPools()) ||
            !matchesBigDecimalRange(info.areaSqm(), details.getAreaSqm())) {
            return false;
        }

        return matchesBooleanAndSeasonFilters(info, details);
    }

    private boolean matchesBooleanAndSeasonFilters(CatalogInfoInput info, PropertyInformationDetails details) {
        if (info.nextToBeach() != null && !info.nextToBeach().equals(details.isNextToBeach())) return false;
        if (info.petFriendly() != null && !info.petFriendly().equals(details.isPetFriendly())) return false;
        if (info.hasWifi() != null && !info.hasWifi().equals(details.isHasWifi())) return false;
        if (info.hasAirConditioning() != null && !info.hasAirConditioning().equals(details.isHasAirConditioning())) return false;
        return info.favorableSeason() == null || info.favorableSeason().equals(details.getFavorableSeason());
    }

    private boolean matchesIntRange(RangeInput<Integer> range, Integer value) {
        if (range == null) return true;
        if (value == null) return false;
        return (range.min() == null || value >= range.min()) && (range.max() == null || value <= range.max());
    }

    private boolean matchesBigDecimalRange(RangeInput<BigDecimal> range, BigDecimal value) {
        if (range == null) return true;
        if (value == null) return false;
        return (range.min() == null || value.compareTo(range.min()) >= 0) && (range.max() == null || value.compareTo(range.max()) <= 0);
    }
}
