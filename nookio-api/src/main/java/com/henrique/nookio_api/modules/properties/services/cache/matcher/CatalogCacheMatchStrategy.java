package com.henrique.nookio_api.modules.properties.services.cache.matcher;

import com.henrique.nookio_api.modules.properties.dto.InputCatalog;
import com.henrique.nookio_api.modules.properties.models.VwPropertiesCatalog;

public interface CatalogCacheMatchStrategy {

    boolean matches(InputCatalog search, VwPropertiesCatalog property);
}
