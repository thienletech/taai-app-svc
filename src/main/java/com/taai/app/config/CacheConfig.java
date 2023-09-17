package com.taai.app.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
                "stockCacheByTicker",
                "stocksCacheByKeyword",
                "stockPredictionsByTicker",
                "stockInfoByTicker");
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Caching(
            evict = {
                    @CacheEvict(cacheNames = "stockCacheByTicker", allEntries = true),
                    @CacheEvict(cacheNames = "stocksCacheByKeyword", allEntries = true)
            }
    )
    public void evictTickerCache() {
        log.info("Evicting ticker cache");
        // Cache will be evicted at the beginning of every day
    }

    @Scheduled(cron = "0 0 */4 * * *")
    @Caching(
            evict = {
                    @CacheEvict(cacheNames = "stockPredictionsByTicker", allEntries = true),
                    @CacheEvict(cacheNames = "stockInfoByTicker", allEntries = true)
            }
    )
    public void evictPriceCache() {
        log.info("Evicting price cache");
        // Cache will be evicted at the beginning of every day
    }
}