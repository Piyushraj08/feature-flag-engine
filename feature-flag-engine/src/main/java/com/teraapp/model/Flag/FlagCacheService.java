package com.teraapp.model.Flag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class FlagCacheService {

    private static final String CACHE_PREFIX = "flag:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private FlagRepository flagRepository;

    public Flag getFlag(Long flagId) {
        String cacheKey = CACHE_PREFIX + flagId;

        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached instanceof Flag) {
            return (Flag) cached;
        }

        Flag flag = flagRepository.findById(flagId)
                .orElseThrow(() -> new RuntimeException("Flag not found: " + flagId));

        redisTemplate.opsForValue().set(cacheKey, flag, Duration.ofMinutes(5));
        return flag;
    }

    public void invalidateCache(Long flagId) {
        redisTemplate.delete(CACHE_PREFIX + flagId);
    }
}