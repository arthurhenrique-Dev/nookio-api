package com.henrique.nookio_api.modules.properties.services.cache;

import com.henrique.nookio_api.modules.properties.dto.InputCatalog;
import com.henrique.nookio_api.modules.properties.models.VwPropertiesCatalog;
import com.henrique.nookio_api.modules.properties.services.cache.matcher.CatalogCacheMatchStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CatalogCacheMatcher {

    private final List<CatalogCacheMatchStrategy> strategies;

    public CatalogCacheMatcher(List<CatalogCacheMatchStrategy> strategies) {
        this.strategies = strategies;
    }

    public boolean matches(InputCatalog search, VwPropertiesCatalog property) {
        if (search == null || (search.search() == null && search.info() == null)) return true;

        if (property == null) return true;

        return strategies.stream()
                .allMatch(strategy -> strategy.matches(search, property));
    }
}
