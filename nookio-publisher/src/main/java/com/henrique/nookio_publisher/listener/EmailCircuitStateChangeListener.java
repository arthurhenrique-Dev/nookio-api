package com.henrique.nookio_publisher.listener;

import com.henrique.nookio_publisher.scheduler.EmailRetryScheduler;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailCircuitStateChangeListener {

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final EmailRetryScheduler emailRetryScheduler;

    @PostConstruct
    public void registerStateTransitionListeners() {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("emailService");
        cb.getEventPublisher().onStateTransition(event -> {
            log.info("[EMAIL_CIRCUIT_EVENT] Transition: {} -> {}", event.getStateTransition().getFromState(), event.getStateTransition().getToState());
            if (event.getStateTransition().getToState() == CircuitBreaker.State.CLOSED) {
                log.info("[EMAIL_CIRCUIT_CLOSED] Email circuit recovered! Immediately resending pending emails...");
                emailRetryScheduler.retryPendingEmails();
            }
        });
    }
}
