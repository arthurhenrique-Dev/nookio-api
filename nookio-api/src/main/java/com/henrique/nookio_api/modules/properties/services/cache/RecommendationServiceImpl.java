package com.henrique.nookio_api.modules.properties.services.cache;

import com.henrique.nookio_api.modules.properties.models.VwPropertiesCatalog;
import com.henrique.nookio_api.modules.properties.repository.VwPropertiesCatalogRepository;
import com.henrique.nookio_api.shared.config.CaffeineCacheConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final VwPropertiesCatalogRepository catalogRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CaffeineCacheConfig.RECOMMENDED_PROPERTIES_CACHE, key = "'d1_global_recommendations'")
    public List<VwPropertiesCatalog> getD1Recommendations() {
        log.info("[CAFFEINE_CACHE_MISS] Querying database read replicas for D-1 recommendations...");
        var slice = catalogRepository.findAllBy(null, PageRequest.of(0, 50));
        List<VwPropertiesCatalog> recommendations = slice.getContent();
        log.info("[CAFFEINE_CACHE_LOADED] Loaded {} D-1 recommendations into Caffeine L1 RAM Cache", recommendations.size());
        return recommendations;
    }

    @Override
    @CacheEvict(value = CaffeineCacheConfig.RECOMMENDED_PROPERTIES_CACHE, allEntries = true)
    public void evictAndWarmupRecommendations() {
        log.info("[CAFFEINE_CACHE_EVICT] Evicting and warming up D-1 recommendations cache...");
        getD1Recommendations();
    }
}
