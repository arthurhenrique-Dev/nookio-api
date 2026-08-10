package com.henrique.nookio_api.core.health_monitor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Component
public class ApplicationStress {

    private static final double STRESS_LIMIT = 66.6;
    private static final double RETURN_TO_NORMAL = 58;

    private volatile boolean isStressed = false;
    private volatile double usePercentage = 0;
    private volatile LocalDateTime lastCheck = null;
    private volatile Long usedMemory;
    private volatile Long maxMemory = Runtime.getRuntime().maxMemory();

    public void manageStressApplication(Long used, Long max){
        usePercentage = ((double) used / max) * 100;
        lastCheck = LocalDateTime.now();
        usedMemory = used;
        if (RETURN_TO_NORMAL >= usePercentage) isStressed = false;
        if (STRESS_LIMIT >= usePercentage) isStressed = true;
    }
}
