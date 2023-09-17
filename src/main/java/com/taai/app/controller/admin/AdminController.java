package com.taai.app.controller.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.taai.app.dto.common.ResponsePayload;
import com.taai.app.dto.stock.StockPriceDTO;
import com.taai.app.service.stock.StockImportService;
import com.taai.app.service.stock.StockPriceImporterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {
    @Autowired
    private StockPriceImporterService stockPriceImporterService;
    @Autowired
    private StockImportService stockImportService;

    @PostMapping("/import/stock-prices/file")
    public ResponsePayload<String> importStockPricesFile(
            @RequestParam(value = "file") List<MultipartFile> files) throws IOException {
        for (var file : files) {
            stockPriceImporterService.importStockPrices(file);
        }
        return ResponsePayload.ok(null);
    }

    @PostMapping("/import/stock-prices/remote")
    public ResponsePayload<String> importStockPricesRemote(
            @RequestParam(name = "all", defaultValue = "false") boolean updateAll) {
        stockPriceImporterService.importStockPricesFromRemote(updateAll);
        return ResponsePayload.ok(null);
    }

    @PostMapping("/import/stocks/file")
    public ResponsePayload<String> importStocksFile(@RequestParam("file") List<MultipartFile> files) throws IOException {
        for (var file : files) {
            stockImportService.importStock(file);
        }
        return ResponsePayload.ok(null);
    }

    @PostMapping("/import/stocks/remote")
    public ResponsePayload<String> importStocksRemote() throws JsonProcessingException {
        stockImportService.importStocksFromRemote();
        return ResponsePayload.ok(null);
    }

    @GetMapping("/stock-prices")
    public ResponsePayload<List<StockPriceDTO>> getStockPrices(@RequestParam("ticker") String ticker,
                                                               @RequestParam("date") LocalDate date) {
        var items = stockPriceImporterService.searchStockPricesBy(StockPriceDTO.builder()
                .ticker(ticker)
                .date(date).build());
        return ResponsePayload.ok(items);
    }

    @DeleteMapping("/cache/stocks")
    @Caching(evict = {
            @CacheEvict(value = "stockCacheByTicker", allEntries = true),
            @CacheEvict(value = "stocksCacheByKeyword", allEntries = true)
    })
    public ResponsePayload<String> deleteStocksCache() {
        log.info("Deleting all stock prices");
        return ResponsePayload.ok(null);
    }

    @DeleteMapping("/cache/stock-prices")
    @Caching(evict = {
            @CacheEvict(value = "stockInfoByTicker", allEntries = true),
            @CacheEvict(value = "stockPredictionsByTicker", allEntries = true)
    })
    public ResponsePayload<String> deleteStockPricesCache() {
        log.info("Deleting all stock prices cache");
        return ResponsePayload.ok(null);
    }
}
