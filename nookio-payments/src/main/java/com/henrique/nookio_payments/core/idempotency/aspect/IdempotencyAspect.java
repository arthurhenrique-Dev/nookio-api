package com.henrique.nookio_payments.core.idempotency.aspect;

import com.henrique.nookio_payments.core.idempotency.annotation.Idempotency;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;

@Aspect
@Component
public class IdempotencyAspect {

    private static final String PREFIX = "idempotency:payments:";
    private static final Duration TTL = Duration.ofMinutes(3);

    private final RedisTemplate<String, Object> redisTemplate;

    public IdempotencyAspect(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Around("@annotation(idempotency)")
    public Object idempotency(ProceedingJoinPoint joinPoint, Idempotency idempotency) throws Throwable {
        HttpServletRequest request = getCurrentRequest();
        String idempotencyKey = request.getHeader("Idempotency-Key");

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("Header 'Idempotency-Key' is required for payment processing.");
        }

        String redisKey = PREFIX + idempotencyKey;
        boolean created = Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(redisKey, "", TTL));

        if (!created) {
            throw new IllegalStateException("A request with this Idempotency-Key is already being processed.");
        }

        try {
            return joinPoint.proceed();
        } finally {
            if (idempotency.desarmPostOperation()) redisTemplate.delete(redisKey);
        }
    }

    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) throw new IllegalStateException("No HTTP request is currently active.");
        return attributes.getRequest();
    }
}
