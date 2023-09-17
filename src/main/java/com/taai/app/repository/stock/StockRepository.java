package com.taai.app.repository.stock;

import com.taai.app.domain.stock.Stock;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    @Cacheable(cacheNames = "stockCacheByTicker", key = "#ticker")
    Stock findByTicker(String ticker);

    List<Stock> findByTickerContainingIgnoreCaseOrderByTickerAsc(String ticker);

    @Cacheable(cacheNames = "stocksCacheByKeyword", key = "#keyword")
    default List<Stock> findAutocomplete(String keyword) {
        return findByTickerContainingIgnoreCaseOrderByTickerAsc(keyword);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "stockCacheByTicker", key = "#stock.ticker"),
            @CacheEvict(cacheNames = "stocksCacheByKeyword", allEntries = true)
    })
    default Stock saveStock(Stock stock) {
        return save(stock);
    }
}
