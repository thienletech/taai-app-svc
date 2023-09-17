package com.taai.app.repository.remote;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.taai.app.dto.stock.StockDTO;
import com.taai.app.dto.stock.StockPriceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class FirstDataSource implements DataSource {
    @Autowired
    private FirstDatasourceClient client;

    @Override
    public List<StockDTO> fetchStocks() throws JsonProcessingException {
        var hsxStocks = fetchStocksByMarket("HSX");
        var hnxStocks = fetchStocksByMarket("HNX");
        var upcomStocks = fetchStocksByMarket("UPCOM");
        return Stream.of(hsxStocks, hnxStocks, upcomStocks).flatMap(List::stream).toList();
    }

    private List<StockDTO> fetchStocksByMarket(String market) throws JsonProcessingException {
        var content = client.getDetailStockByType(market);
        return FirstDSStockResponseMapper.mapResponseToStocks(content);
    }

    @Override
    public List<StockPriceDTO> fetchStockPrices() {
        return List.of();
    }
}
