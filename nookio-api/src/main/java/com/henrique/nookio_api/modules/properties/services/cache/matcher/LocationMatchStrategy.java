package com.henrique.nookio_api.modules.properties.services.cache.matcher;

import com.henrique.nookio_api.modules.location.dto.LocationInput;
import com.henrique.nookio_api.modules.location.models.Location;
import com.henrique.nookio_api.modules.location.models.LocationInformation;
import com.henrique.nookio_api.modules.properties.dto.InputCatalog;
import com.henrique.nookio_api.modules.properties.models.VwPropertiesCatalog;
import org.springframework.stereotype.Component;

@Component
public class LocationMatchStrategy implements CatalogCacheMatchStrategy {

    @Override
    public boolean matches(InputCatalog search, VwPropertiesCatalog property) {
        if (search == null || search.info() == null || search.info().locationInput() == null) return true;

        Location locEntity = property.getLocation();
        LocationInformation locInfo = locEntity != null ? locEntity.getLocationInformation() : null;
        if (locInfo == null) return true;

        LocationInput locInput = search.info().locationInput();

        if (locInput.city() != null && !locInput.city().equalsIgnoreCase(locInfo.getCity())) return false;
        if (locInput.state() != null && !locInput.state().equalsIgnoreCase(locInfo.getState())) return false;
        if (locInput.neighborhood() != null && !locInput.neighborhood().equalsIgnoreCase(locInfo.getNeighborhood())) return false;
        if (locInput.street() != null && !locInput.street().equalsIgnoreCase(locInfo.getStreet())) return false;
        if (locInput.zipCode() != null && !locInput.zipCode().equalsIgnoreCase(locInfo.getZipCode())) return false;
        return locInput.country() == null || locInput.country().equalsIgnoreCase(locInfo.getCountry());
    }
}
