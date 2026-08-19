package com.henrique.nookio_api.modules.properties.scheduler;

import com.henrique.nookio_api.modules.properties.services.cache.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationWarmupRunner implements ApplicationRunner {

    private final RecommendationService recommendationService;

    @Override
    public void run(ApplicationArguments args) {
        warmupRecommendations("[STARTUP]");
    }

    /**
     * Daily Proactive Warmup: Runs every night at 03:00 AM (after D-1 batch completion)
     * or can be triggered via fixedRate (24 hours).
     */
    @Scheduled(cron = "0 0 4 * * *")
    public void scheduledDailyWarmup() {
        warmupRecommendations("[SCHEDULED_DAILY_4AM]");
    }

    private void warmupRecommendations(String triggerSource) {
        log.info("[CAFFEINE_WARMUP_STARTED] {} Warming up D-1 recommendations cache...", triggerSource);
        try {
            recommendationService.evictAndWarmupRecommendations();
            log.info("[CAFFEINE_WARMUP_SUCCESS] {} Pre-warmed D-1 recommendations in Caffeine L1 Cache!", triggerSource);
        } catch (Exception e) {
            log.warn("[CAFFEINE_WARMUP_WARNING] {} Could not pre-warm recommendations: {}", triggerSource, e.getMessage());
        }
    }
}
