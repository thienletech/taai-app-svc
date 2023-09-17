package com.taai.app.repository.stock;

import com.taai.app.domain.stock.StockPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StockPredictionRepository extends JpaRepository<StockPrediction, Long> {
    List<StockPrediction> findByTickerAndDateAfterOrderByDateAsc(String ticker, LocalDate date);

    default List<StockPrediction> findByTickerAndDateAfter(String ticker, LocalDate date) {
        return findByTickerAndDateAfterOrderByDateAsc(ticker, date);
    }
}
