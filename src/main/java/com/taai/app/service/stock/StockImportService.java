package com.taai.app.service.stock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.taai.app.config.ExecutionTime;
import com.taai.app.repository.remote.FirstDSStockResponseMapper;
import com.taai.app.dto.stock.StockDTO;
import com.taai.app.mapper.stock.StockMapper;
import com.taai.app.repository.remote.RemoteDataSource;
import com.taai.app.repository.stock.StockRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class StockImportService {
    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private RemoteDataSource remoteDataSource;

    @ExecutionTime
    public void importStock(MultipartFile file) throws IOException {
        log.info("Importing stock prices from {}", file.getOriginalFilename());
        var stocks = parseStockPrices(file);
        stocks.forEach(this::normalizeData);
        stocks.forEach(this::importStock);
    }

    private void normalizeData(StockDTO stockDTO) {
        stockDTO.setTicker(stockDTO.getTicker().toUpperCase());
    }

    private void importStock(StockDTO stockDTO) {
        var exist = stockRepository.findByTicker(stockDTO.getTicker());
        if (exist != null) {
            exist.setName(stockDTO.getName());
            exist.setMarket(stockDTO.getMarket());
            log.info("Updated stock {}", exist);
            stockRepository.saveStock(exist);
        } else {
            var stock = StockMapper.INSTANCE.toEntity(stockDTO);
            stock = stockRepository.saveStock(stock);
            log.info("Insert stock {}", stock);
        }
    }

    private Collection<StockDTO> parseStockPrices(MultipartFile file) throws IOException {
        var uri = file.getOriginalFilename();
        if (!StringUtils.hasText(uri)) {
            return List.of();
        }
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        return FirstDSStockResponseMapper.mapResponseToStocks(content);
    }

    @Async
    @ExecutionTime
    public void importStocksFromRemote() throws JsonProcessingException {
        var stocks = remoteDataSource.fetchStocks();
        stocks.forEach(this::normalizeData);
        stocks.forEach(this::importStock);
    }
}
