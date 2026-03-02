package com.stephen_oosthuizen.genesis.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Cache configuration for template caching.
 * Uses Caffeine for high-performance in-memory caching.
 */
@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        log.info("Configuring Caffeine cache manager");

        CaffeineCacheManager cacheManager = new CaffeineCacheManager("templates", "metadata");

        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .recordStats() // Enable cache statistics
        );

        log.info("Cache manager configured: max size=100, TTL=1 hour");
        return cacheManager;
    }
}
