package com.henrique.nookio_api.modules.properties.scheduler;

import com.henrique.nookio_api.modules.properties.services.analytics.SearchLogRetryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchLogRetryScheduler {

    private final SearchLogRetryService searchLogRetryService;

    @Scheduled(fixedDelay = 15000)
    public void processPendingLogsInBatch() {
        searchLogRetryService.processPendingLogsInBatch();
    }
}
