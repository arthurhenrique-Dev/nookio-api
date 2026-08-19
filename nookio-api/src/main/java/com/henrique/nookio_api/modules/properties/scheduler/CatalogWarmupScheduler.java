package com.henrique.nookio_api.modules.properties.scheduler;

import com.henrique.nookio_api.modules.properties.dto.InputCatalog;
import com.henrique.nookio_api.modules.properties.models.PrioritySearch;
import com.henrique.nookio_api.modules.properties.repository.PrioritySearchRepository;
import com.henrique.nookio_api.modules.properties.services.PropertiesService;
import com.henrique.nookio_api.modules.properties.services.cache.canonicalizer.CatalogSearchCanonicalizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CatalogWarmupScheduler {

    private final PrioritySearchRepository prioritySearchRepository;
    private final CatalogSearchCanonicalizer canonicalizer;
    private final PropertiesService propertiesService;

    @Scheduled(cron = "0 0 4 * * *")
    public void warmupTop30Searches() {
        log.info("Starting daily catalog cache warmup for TOP 30 priority searches...");

        List<PrioritySearch> topSearches = prioritySearchRepository.findTop30ByOrderBySearchsDescTimestampDesc();
        if (topSearches.isEmpty()) {
            log.info("No priority searches found in database for warmup.");
            return;
        }

        int count = 0;
        for (PrioritySearch search : topSearches) {
            try {
                String params = search.getParams();
                log.info("Warmup [{}]: Pre-warming catalog cache for search params: {}", ++count, params);
                InputCatalog inputCatalog = canonicalizer.deserialize(params);
                propertiesService.warmupCatalog(inputCatalog);
            } catch (Exception e) {
                log.error("Failed to pre-warm catalog cache for params: {}", search.getParams(), e);
            }
        }

        log.info("Daily catalog cache warmup completed successfully. {} searches warmed up.", count);
    }
}
