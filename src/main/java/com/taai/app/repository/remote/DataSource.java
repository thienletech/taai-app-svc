package com.taai.app.repository.remote;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.taai.app.dto.stock.StockDTO;
import com.taai.app.dto.stock.StockPriceDTO;

import java.util.List;

public interface DataSource {
    List<StockDTO> fetchStocks() throws JsonProcessingException;

    List<StockPriceDTO> fetchStockPrices();
}
