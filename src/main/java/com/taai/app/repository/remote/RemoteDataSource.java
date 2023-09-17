package com.taai.app.repository.remote;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.taai.app.dto.stock.StockDTO;
import com.taai.app.dto.stock.StockPriceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class RemoteDataSource implements DataSource {
    @Autowired
    private FirstDataSource firstDataSource;
    @Autowired
    private SecondDataSource secondDataSource;

    @Override
    public List<StockDTO> fetchStocks() throws JsonProcessingException {
        return Stream.of(firstDataSource.fetchStocks(), secondDataSource.fetchStocks())
                .flatMap(List::stream).toList();
    }

    @Override
    public List<StockPriceDTO> fetchStockPrices() {
        return secondDataSource.fetchStockPrices();
    }
}
