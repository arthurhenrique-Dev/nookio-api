package com.henrique.nookio_api.modules.properties.services.cache;

import com.henrique.nookio_api.modules.properties.models.VwPropertiesCatalog;
import java.util.List;

public interface RecommendationService {
    List<VwPropertiesCatalog> getD1Recommendations();
    void evictAndWarmupRecommendations();
}
