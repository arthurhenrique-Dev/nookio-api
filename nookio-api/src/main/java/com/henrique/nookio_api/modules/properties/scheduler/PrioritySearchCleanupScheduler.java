package com.henrique.nookio_api.modules.properties.scheduler;

import com.henrique.nookio_api.modules.properties.repository.PrioritySearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrioritySearchCleanupScheduler {

    private final PrioritySearchRepository prioritySearchRepository;

    @Scheduled(cron = "0 0 3 * * SUN")
    public void cleanupWeeklyPrioritySearches() {
        log.info("Executing weekly TRUNCATE on properties.priority table to reset recent search window...");
        try {
            prioritySearchRepository.truncateTable();
            log.info("Successfully truncated properties.priority table.");
        } catch (Exception e) {
            log.error("Failed to execute weekly TRUNCATE on properties.priority table", e);
        }
    }
}
