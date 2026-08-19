package com.henrique.nookio_api.modules.properties.services.cache.store;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.function.Consumer;


@Slf4j
@Repository
@RequiredArgsConstructor
public class CatalogCacheStore {

    private static final String CACHE_PREFIX = "properties-catalog::";
    private static final int SCAN_COUNT = 100;

    private final RedisTemplate<String, Object> redisTemplate;

    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.warn("Failed to get cache entry from Redis for key: {}", key, e);
            return null;
        }
    }

    public void put(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            log.warn("Failed to store cache entry in Redis for key: {}", key, e);
        }
    }

    public void put(String key, Object value, Duration ttl) {
        try {
            redisTemplate.opsForValue().set(key, value, ttl);
        } catch (Exception e) {
            log.warn("Failed to store cache entry in Redis for key: {} with TTL: {}", key, ttl, e);
        }
    }

    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.warn("Failed to delete key from Redis: {}", key, e);
        }
    }

    public void scanKeys(Consumer<String> keyConsumer) {
        ScanOptions options = ScanOptions.scanOptions()
                .match(CACHE_PREFIX + "*")
                .count(SCAN_COUNT)
                .build();

        try (Cursor<String> cursor = redisTemplate.scan(options)) {
            while (cursor.hasNext()) keyConsumer.accept(cursor.next());
        } catch (Exception e) {
            log.warn("Failed to scan keys matching pattern: {}", CACHE_PREFIX + "*", e);
        }
    }

    public String buildKey(String canonicalParams) {
        return CACHE_PREFIX + canonicalParams;
    }

    public String extractCanonicalParams(String key) {
        if (key != null && key.startsWith(CACHE_PREFIX)) return key.substring(CACHE_PREFIX.length());
        return key;
    }
}
