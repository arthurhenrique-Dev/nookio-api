package com.henrique.nookio_api.core.idempotency.aspect;

import com.henrique.nookio_api.core.idempotency.annotation.Idempotency;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Aspect
@Component
@RequiredArgsConstructor
public class IdempotencyAspect {

    private final RedisTemplate redisTemplate;

    @Around("@annotation(idempotency)")
    public Object idempotency(ProceedingJoinPoint joinPoint, Idempotency idempotency) throws Throwable{

        String IDEMPOTENCY_KEY = "idempotency:" + itemsToKey(joinPoint.getArgs());
        ValueOperations operator = redisTemplate.opsForValue();
        Boolean created = operator.setIfAbsent(IDEMPOTENCY_KEY, "", Duration.ofMinutes(3));
        if (!created) return null;
        try {
            return joinPoint.proceed();
        } finally {
            if(idempotency.desarmPostOperation()) redisTemplate.delete(IDEMPOTENCY_KEY);
        }
    }

    private String itemsToKey(Object[] items){
        String key = "";
        for (Object item : items){
            key += (key.isEmpty() ? "" : ":") + item.hashCode();
        }
        return key;
    }
}
