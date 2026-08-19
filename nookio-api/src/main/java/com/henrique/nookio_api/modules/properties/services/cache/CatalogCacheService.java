package com.henrique.nookio_api.modules.properties.services.cache;

import com.henrique.nookio_api.modules.properties.dto.InputCatalog;
import com.henrique.nookio_api.modules.properties.models.VwPropertiesCatalog;
import com.henrique.nookio_api.modules.properties.services.cache.canonicalizer.CatalogSearchCanonicalizer;
import com.henrique.nookio_api.modules.properties.services.cache.store.CatalogCacheStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogCacheService {

    private static final Duration TTL_NORMAL = Duration.ofHours(2);
    private static final Duration TTL_WARMUP = Duration.ofHours(24);

    private final CatalogCacheStore store;
    private final CatalogSearchCanonicalizer canonicalizer;

    @SuppressWarnings("unchecked")
    public Slice<VwPropertiesCatalog> get(InputCatalog input) {
        String canonicalParams = canonicalizer.serialize(input);
        String key = store.buildKey(canonicalParams);

        Object cachedObj = store.get(key);
        if (cachedObj instanceof Slice<?>) {
            log.debug("Catalog cache HIT for key: {}", key);
            return (Slice<VwPropertiesCatalog>) cachedObj;
        }

        return null;
    }

    public void put(InputCatalog input, Slice<VwPropertiesCatalog> result) {
        String canonicalParams = canonicalizer.serialize(input);
        String key = store.buildKey(canonicalParams);
        log.info("Caching catalog search [normal, TTL=2h] for params: {}", canonicalParams);
        store.put(key, result, TTL_NORMAL);
    }

    public void putWarmup(InputCatalog input, Slice<VwPropertiesCatalog> result) {
        String canonicalParams = canonicalizer.serialize(input);
        String key = store.buildKey(canonicalParams);
        log.info("Caching catalog search [warmup, TTL=24h] for params: {}", canonicalParams);
        store.put(key, result, TTL_WARMUP);
    }
}
