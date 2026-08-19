package com.henrique.nookio_api.core.audit_logs.listener;

import com.henrique.nookio_api.core.audit_logs.scheduler.AuditLogDispatcherScheduler;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CircuitBreakerStateChangeListener {

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final AuditLogDispatcherScheduler dispatcherScheduler;

    @PostConstruct
    public void registerStateTransitionListeners() {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("analyticsStompService");
        cb.getEventPublisher().onStateTransition(event -> {
            log.info("[CIRCUIT_BREAKER_EVENT] Transition: {} -> {}", event.getStateTransition().getFromState(), event.getStateTransition().getToState());
            if (event.getStateTransition().getToState() == CircuitBreaker.State.CLOSED) {
                log.info("[CIRCUIT_BREAKER_CLOSED] Circuit recovered! Immediately flushing pending local audit logs...");
                dispatcherScheduler.dispatchPendingAuditLogs();
            }
        });
    }
}
