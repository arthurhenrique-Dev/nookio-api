package com.henrique.nookio_api.modules.properties.services.cache.matcher;

import com.henrique.nookio_api.modules.location.models.Location;
import com.henrique.nookio_api.modules.location.models.LocationInformation;
import com.henrique.nookio_api.modules.properties.dto.InputCatalog;
import com.henrique.nookio_api.modules.properties.models.VwPropertiesCatalog;
import org.springframework.stereotype.Component;

@Component
public class TextSearchMatchStrategy implements CatalogCacheMatchStrategy {

    @Override
    public boolean matches(InputCatalog search, VwPropertiesCatalog property) {
        if (search == null || search.search() == null || search.search().isBlank()) return true;

        String searchLower = search.search().toLowerCase();
        String owner = property.getOwnerName() != null ? property.getOwnerName().toLowerCase() : "";

        Location locEntity = property.getLocation();
        LocationInformation locInfo = locEntity != null ? locEntity.getLocationInformation() : null;

        String city = locInfo != null && locInfo.getCity() != null ? locInfo.getCity().toLowerCase() : "";
        String state = locInfo != null && locInfo.getState() != null ? locInfo.getState().toLowerCase() : "";
        String neighborhood = locInfo != null && locInfo.getNeighborhood() != null ? locInfo.getNeighborhood().toLowerCase() : "";

        return owner.contains(searchLower) || city.contains(searchLower) || state.contains(searchLower) || neighborhood.contains(searchLower);
    }
}
