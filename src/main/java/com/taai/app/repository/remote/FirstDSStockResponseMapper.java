package com.taai.app.repository.remote;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.taai.app.dto.remote.FirstDSStockResponseDTO;
import com.taai.app.dto.stock.StockDTO;
import com.taai.app.util.JsonUtil;

import java.util.List;

public class FirstDSStockResponseMapper {

    private FirstDSStockResponseMapper() {
    }

    public static List<StockDTO> mapResponseToStocks(String response) throws JsonProcessingException {
        var responseDTO = JsonUtil.fromJson(response, FirstDSStockResponseDTO.class);
        return parse(responseDTO);
    }

    private static List<StockDTO> parse(FirstDSStockResponseDTO response) {
        return response.getArrDetailStock().stream().map(FirstDSStockResponseMapper::parse).toList();
    }

    private static StockDTO parse(String encodedStock) {
        var parts = encodedStock.split("\\|");
        return StockDTO.builder()
                .ticker(parts[0])
                .market(parts[1])
                .name(parts[parts.length - 1])
                .build();
    }
}
