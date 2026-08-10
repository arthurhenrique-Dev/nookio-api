package com.henrique.nookio_api.modules.properties.services.cache;

import com.henrique.nookio_api.modules.properties.dto.InputCatalog;
import com.henrique.nookio_api.modules.properties.models.VwPropertiesCatalog;
import com.henrique.nookio_api.modules.properties.repository.VwPropertiesCatalogRepository;
import com.henrique.nookio_api.modules.properties.services.cache.canonicalizer.CatalogSearchCanonicalizer;
import com.henrique.nookio_api.modules.properties.services.cache.store.CatalogCacheStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CatalogCacheInvalidator {

    private final CatalogCacheStore store;
    private final CatalogSearchCanonicalizer canonicalizer;
    private final CatalogCacheMatcher matcher;
    private final VwPropertiesCatalogRepository catalogRepository;

    public void invalidateAffectedCaches(Integer propertyId) {
        if (propertyId == null) return;

        Optional<VwPropertiesCatalog> catalogOpt = catalogRepository.findById(propertyId);
        VwPropertiesCatalog catalogItem = catalogOpt.orElse(null);

        log.info("Starting streaming SCAN for selective cache invalidation (Property ID: {})...", propertyId);

        store.scanKeys(key -> processKeyInvalidation(key, propertyId, catalogItem));
    }

    private void processKeyInvalidation(String key, Integer propertyId, VwPropertiesCatalog catalogItem) {
        String canonicalParams = store.extractCanonicalParams(key);

        if (!shouldEvict(canonicalParams, catalogItem)) return;

        store.delete(key);
        log.info("Selectively evicted catalog cache entry '{}' for modified Property ID {}.", key, propertyId);
    }

    private boolean shouldEvict(String canonicalParams, VwPropertiesCatalog catalogItem) {
        if ("all".equalsIgnoreCase(canonicalParams) || catalogItem == null) return true;

        try {
            InputCatalog search = canonicalizer.deserialize(canonicalParams);
            return matcher.matches(search, catalogItem);
        } catch (Exception e) {
            log.warn("Failed to evaluate search matching for canonical key '{}', defaulting to evict for safety.", canonicalParams, e);
            return true;
        }
    }
}
