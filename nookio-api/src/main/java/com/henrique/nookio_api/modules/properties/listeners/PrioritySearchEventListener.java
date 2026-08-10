package com.henrique.nookio_api.modules.properties.listeners;

import com.henrique.nookio_api.modules.properties.events.CatalogSearchEvent;
import com.henrique.nookio_api.modules.properties.repository.PrioritySearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PrioritySearchEventListener {

    private final PrioritySearchRepository prioritySearchRepository;

    @Async
    @EventListener
    public void handleCatalogSearchEvent(CatalogSearchEvent event) {
        if (event == null || event.canonicalParams() == null || event.canonicalParams().isBlank()) {
            return;
        }

        try {
            prioritySearchRepository.incrementSearchCount(event.canonicalParams(), event.timestamp());
            log.debug("Incremented priority search count for canonical params: {}", event.canonicalParams());
        } catch (Exception e) {
            log.error("Failed to increment priority search count for params: {}", event.canonicalParams(), e);
        }
    }
}
