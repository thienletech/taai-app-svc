package com.taai.app.service.stock;

import com.taai.app.config.ExecutionTime;
import com.taai.app.dto.stock.StockDTO;
import com.taai.app.dto.stock.StockPriceDTO;
import com.taai.app.mapper.stock.StockMapper;
import com.taai.app.mapper.stock.StockPriceMapper;
import com.taai.app.repository.remote.RemoteDataSource;
import com.taai.app.repository.stock.StockPriceRepository;
import com.taai.app.repository.stock.StockRepository;
import com.taai.app.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.taai.app.util.TimeUtil.DATE_FORMAT;

@Service
@Slf4j
public class StockPriceImporterService {
    @Autowired
    private StockPriceRepository stockPriceRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private RemoteDataSource remoteDataSource;

    @ExecutionTime
    public void importStockPrices(MultipartFile file) throws IOException {
        log.info("Importing stock prices from {}", file.getOriginalFilename());
        var stockPrices = parseStockPrices(file);
        stockPrices.forEach(this::normalizeData);
        stockPrices.forEach(this::importStock);
        stockPrices.forEach(this::importStockPrice);
    }

    private Collection<StockPriceDTO> parseStockPrices(MultipartFile file) throws IOException {
        List<StockPriceDTO> stockPrices = new ArrayList<>();
        var uri = file.getOriginalFilename();
        if (!StringUtils.hasText(uri)) {
            return stockPrices;
        }
        try (
                var csvParser = new CSVParser(new InputStreamReader(file.getInputStream()),
                        CSVFormat.Builder.create()
                                .setIgnoreEmptyLines(true)
                                .setIgnoreSurroundingSpaces(true)
                                .setSkipHeaderRecord(true)
                                .setTrim(true)
                                .build())
        ) {
            csvParser.stream().skip(1).forEach(row -> {
                var dto = StockPriceDTO.builder()
                        .ticker(row.get(0))
                        .date(TimeUtil.parseDate(row.get(1), DATE_FORMAT))
                        .open(Double.parseDouble(row.get(2)))
                        .high(Double.parseDouble(row.get(3)))
                        .low(Double.parseDouble(row.get(4)))
                        .close(Double.parseDouble(row.get(5)))
                        .volume(Double.parseDouble(row.get(6)))
                        .build();
                stockPrices.add(dto);
            });
        }

        return stockPrices;
    }

    private void normalizeData(StockPriceDTO stockPriceDTO) {
        stockPriceDTO.setTicker(stockPriceDTO.getTicker().toUpperCase());
    }

    private void importStock(StockPriceDTO stockPriceDTO) {
        var stockDTO = StockDTO.builder().ticker(stockPriceDTO.getTicker()).build();
        if (stockRepository.findByTicker(stockDTO.getTicker()) != null) {
            return;
        }
        var stock = StockMapper.INSTANCE.toEntity(stockDTO);
        stock = stockRepository.saveStock(stock);
        log.info("Imported stock {}", stock);
    }

    private void importStockPrice(StockPriceDTO stockPriceDTO) {
        var stockPrice = StockPriceMapper.INSTANCE.toEntity(stockPriceDTO);
        var existing = stockPriceRepository.findByDTO(stockPriceDTO);
        if (!existing.isEmpty()) {
            log.debug("Stock price already exists {}", stockPriceDTO);
            return;
        }
        stockPrice = stockPriceRepository.saveStockPrice(stockPrice);
        log.debug("Imported stock price {}", stockPrice);
    }

    public List<StockPriceDTO> searchStockPricesBy(StockPriceDTO stockPriceDTO) {
        var stockPrices = stockPriceRepository.findByDTO(stockPriceDTO);
        log.info("Found {} stock prices", stockPrices.size());
        return stockPrices.stream().map(StockPriceMapper.INSTANCE::toDTO).toList();
    }

    @Async
    @ExecutionTime
    public void importStockPricesFromRemote(boolean isUpdateAll) {
        log.info("import stock prices from remote source {}", isUpdateAll);
        var stockPrices = remoteDataSource.fetchStockPrices();
        if (!isUpdateAll) {
            var today = LocalDate.now();
            var begin = today.minusMonths(3);
            stockPrices = stockPrices.stream()
                    .filter(stockPriceDTO -> stockPriceDTO.getDate().isAfter(begin)).toList();
        }
        log.info("Found {} stock prices", stockPrices.size());
        stockPrices.forEach(this::normalizeData);
        log.info("Normalized stock prices");
        stockPrices.forEach(this::importStock);
        log.info("Imported stocks");
        stockPrices.forEach(this::importStockPrice);
        log.info("Imported stock prices");
    }
}
