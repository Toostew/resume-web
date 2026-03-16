package com.toostew.resume_web.config;


import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("repos");
        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }

    // This is where you set your portfolio's specific rules
    Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .initialCapacity(10)
                .maximumSize(100)
                // Data expires 1 hour after it's fetched from GitHub
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .recordStats();
    }
}
