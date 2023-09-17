package com.taai.app.repository.stock;

import com.taai.app.domain.stock.StockPrice;
import com.taai.app.domain.stock.StockPrice52Week;
import com.taai.app.dto.stock.StockPriceDTO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {
    List<StockPrice> findByTickerOrderByDateAsc(String ticker);

    List<StockPrice> findByTickerAndDate(String ticker, LocalDate date);

    default List<StockPrice> findByDTO(StockPriceDTO dto) {
        return findByTickerAndDate(dto.getTicker(), dto.getDate());
    }

    @Query("select max(sp.high) as high52w, " +
            "min(sp.low) as low52w, " +
            "sum(sp.volume) as totalVolume52w, " +
            "count(sp.id) as totalCount52w " +
            "from StockPrice sp " +
            "where sp.ticker = :ticker " +
            "and sp.date between :startDate and :endDate ")
    Optional<StockPrice52Week> findPrice52WeekByTicker(@Param("ticker") String ticker,
                                                       @Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);

    default Optional<StockPrice52Week> findPrice52WeekByTicker(String ticker) {
        var today = LocalDate.now();
        var startDate = today.minusWeeks(52);
        var nextDate = today.plusDays(1);
        return findPrice52WeekByTicker(ticker, startDate, nextDate);
    }

    @Query("select sp from StockPrice sp where sp.ticker = :ticker order by sp.date desc limit 2")
    List<StockPrice> findLatestStockPrice(@Param("ticker") String ticker);

    @Caching(
            evict = {
                    @CacheEvict(cacheNames = "stockInfoByTicker", key = "#stockPrice.ticker"),
                    @CacheEvict(cacheNames = "stockPredictionsByTicker", key = "#stockPrice.ticker"),
            }
    )
    default StockPrice saveStockPrice(StockPrice stockPrice) {
        return save(stockPrice);
    }
}
