package com.taai.app.service.stock;

import com.taai.app.config.ExecutionTime;
import com.taai.app.domain.stock.Stock;
import com.taai.app.domain.stock.StockPrediction;
import com.taai.app.domain.stock.StockPrice;
import com.taai.app.dto.common.AppError;
import com.taai.app.dto.stock.StockDTO;
import com.taai.app.dto.stock.StockInfoDTO;
import com.taai.app.dto.stock.StockPredictionResultDTO;
import com.taai.app.dto.stock.StockPriceDTO;
import com.taai.app.mapper.stock.StockMapper;
import com.taai.app.mapper.stock.StockPriceMapper;
import com.taai.app.repository.stock.StockAlgoRepository;
import com.taai.app.repository.stock.StockPredictionRepository;
import com.taai.app.repository.stock.StockPriceRepository;
import com.taai.app.repository.stock.StockRepository;
import com.taai.app.util.NumberUtil;
import com.taai.app.util.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Stream;

import static com.taai.app.util.AppConst.EMPTY_TEXT;

@Service
@Slf4j
public class StockPriceService {
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private StockPriceRepository stockPriceRepository;
    @Autowired
    private StockPredictionRepository stockPredictionRepository;
    @Autowired
    private StockAlgoRepository stockAlgoRepository;

    @ExecutionTime
    public List<StockDTO> getAutocomplete(String query) {
        log.info("query: {}", query);
        return stockRepository.findAutocomplete(StringUtils.hasText(query) ? query : EMPTY_TEXT)
                .stream()
                .filter(this::validStock)
                .map(StockMapper.INSTANCE::toDTO)
                .toList();
    }

    private boolean validStock(Stock stock) {
        if (!StringUtils.hasText(stock.getMarket())) {
            return false;
        }
        return !stock.getTicker().matches("C[a-zA-Z]+\\d{4}");
    }

    @ExecutionTime
    @Cacheable(cacheNames = "stockPredictionsByTicker", key = "#ticker")
    public StockPredictionResultDTO getStockPrediction(String ticker) {
        var realPrices = stockPriceRepository.findByTickerOrderByDateAsc(ticker);
        var lastRealPrice = realPrices.isEmpty() ? null : realPrices.get(realPrices.size() - 1);
        List<StockPrediction> predictionPrices = lastRealPrice != null ?
                stockPredictionRepository.findByTickerAndDateAfter(ticker, lastRealPrice.getDate()) : List.of();
        var realClosePrices = realPrices.stream().map(this::convertStockPriceDTO).toList();
        var predictionClosePrices = predictionPrices.stream().map(this::convertStockPriceDTO).toList();
        var prices = Stream.concat(realClosePrices.stream(), predictionClosePrices.stream()).toList();
        return StockPredictionResultDTO.builder()
                .predictionOffset(realClosePrices.size())
                .prices(prices)
                .build();
    }

    private StockPriceDTO convertStockPriceDTO(StockPrice price) {
        return StockPriceDTO.builder()
                .ticker(price.getTicker())
                .date(price.getDate())
                .open(price.getOpen())
                .high(price.getHigh())
                .low(price.getLow())
                .close(price.getClose())
                .volume(price.getVolume())
                .build();
    }

    private StockPriceDTO convertStockPriceDTO(StockPrediction price) {
        return StockPriceDTO.builder()
                .ticker(price.getTicker())
                .date(price.getDate())
                .close(price.getClose())
                .build();
    }

    @ExecutionTime
    @Cacheable(cacheNames = "stockInfoByTicker", key = "#ticker")
    public StockInfoDTO getStockInfo(String ticker) {
        var stockPrice = stockPriceRepository.findLatestStockPrice(ticker);
        if (stockPrice.isEmpty()) {
            throw new AppException(AppError.NOT_FOUND);
        }
        var stockInfoDTO = StockPriceMapper.INSTANCE.toStockInfoDTO(stockPrice.get(0));
        if (stockPrice.size() > 1) {
            stockInfoDTO.setReference(stockPrice.get(1).getClose());
        } else {
            stockInfoDTO.setReference(stockInfoDTO.getClose());
        }

        var stockPrice52Week = stockPriceRepository.findPrice52WeekByTicker(ticker);
        stockPrice52Week.ifPresent(sp -> {
            stockInfoDTO.setHigh52w(sp.getHigh52w());
            stockInfoDTO.setLow52w(sp.getLow52w());
            stockInfoDTO.setVolume52w(NumberUtil.isNullOrZero(sp.getTotalCount52w())
                    ? 0 : sp.getTotalVolume52w() / sp.getTotalCount52w());
            stockInfoDTO.setTotalVolume52w(sp.getTotalVolume52w());
            stockInfoDTO.setTotalCount52w(sp.getTotalCount52w());
        });

        var stockAlgo = stockAlgoRepository.findByTicker(ticker);
        stockAlgo.ifPresent(sa -> {
            if (sa.getRecommendDate() != null) {
                stockInfoDTO.setRecommendContent(sa.getRecommendContent());
                stockInfoDTO.setRecommendDate(sa.getRecommendDate());
            }
            stockInfoDTO.setPredictDate(sa.getPredictDate());
        });
        return stockInfoDTO;
    }

}
