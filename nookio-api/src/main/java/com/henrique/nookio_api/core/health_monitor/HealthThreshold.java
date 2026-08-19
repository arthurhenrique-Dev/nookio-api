package com.henrique.nookio_api.core.health_monitor;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryNotificationInfo;
import java.lang.management.MemoryUsage;
import java.time.Duration;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class HealthThreshold {

    private final ApplicationStress stress;
    private final TaskScheduler taskScheduler;

    private ScheduledFuture<?> checkTask;

    @PostConstruct
    public void init() {
        MemoryMXBean memoryDetails = ManagementFactory.getMemoryMXBean();
        NotificationEmitter emitter = (NotificationEmitter) memoryDetails;

        NotificationListener listener = (notification, handback) -> {
            
            MemoryUsage usage = memoryDetails.getHeapMemoryUsage();
            stress.manageStressApplication(usage.getUsed(), usage.getMax());

            checkAndScheduleLoop(memoryDetails);
        };

        emitter.addNotificationListener(listener, null, null);

        ManagementFactory.getMemoryPoolMXBeans().forEach(pool -> {
            if (pool.isUsageThresholdSupported()) {
                long max = pool.getUsage().getMax();
                if (max > 0) {
                    pool.setUsageThreshold((long) (max * 0.8));
                }
            }
        });
    }

    private synchronized void checkAndScheduleLoop(MemoryMXBean memoryDetails) {
        if (stress.isStressed()) {
            if (checkTask == null || checkTask.isCancelled()) {
                log.warn("⚠️ Stressed Application! changing some strategies to optmize.");
                checkTask = taskScheduler.scheduleAtFixedRate(() -> {
                    MemoryUsage usage = memoryDetails.getHeapMemoryUsage();
                    stress.manageStressApplication(usage.getUsed(), usage.getMax());

                    if (!stress.isStressed()) {
                        log.info("Strategies turning back to performance!");
                        stopCheckTask();
                    }
                }, Duration.ofMinutes(1));
            }
        } else {
            stopCheckTask();
        }
    }

    private synchronized void stopCheckTask() {
        if (checkTask != null && !checkTask.isCancelled()) {
            checkTask.cancel(false);
            checkTask = null;
        }
    }
}