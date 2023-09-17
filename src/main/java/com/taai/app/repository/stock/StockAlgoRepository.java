package com.taai.app.repository.stock;

import com.taai.app.domain.stock.StockAlgo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockAlgoRepository extends JpaRepository<StockAlgo, Long> {
    Optional<StockAlgo> findByTicker(String ticker);

}
