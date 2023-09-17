package com.taai.app.repository.remote;

import com.taai.app.dto.stock.StockDTO;
import com.taai.app.dto.stock.StockPriceDTO;
import com.taai.app.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Component
@Slf4j
public class SecondDataSource implements DataSource {
    @Value("${remote.second-datasource.res-fmt-index}")
    private String resFmtIndex;
    @Value("${remote.second-datasource.res-fmt-data}")
    private String resFmtData;

    private static final String VNINDEX = "VNINDEX";
    private static final String HNX_INDEX = "HNX-INDEX";
    private final List<String> priorityTickers = List.of("ACB", "BCM", "BID", "BVH",
            "CTG", "FPT", "GAS", "GVR", "HDB", "HPG", "MBB", "MSN", "MWG", "NVL", "PDR",
            "PLX", "POW", "SAB", "SSI", "STB", "TCP", "TPB", "VCB", "VHM", "VIB", "VIC",
            "VJC", "VNM", "VPB", "VRE", HNX_INDEX);

    @Autowired
    private SecondDatasourceClient secondDatasourceClient;

    @Autowired
    private SecondDatasourceGClient secondDatasourceGClient;

    @Override
    public List<StockDTO> fetchStocks() {
        log.info("fetch stocks");
        return List.of(new StockDTO(VNINDEX, VNINDEX, "HSX"),
                new StockDTO(HNX_INDEX, HNX_INDEX, "HNX"));
    }

    @Override
    public List<StockPriceDTO> fetchStockPrices() {
        log.info("fetch latest prices");
        var vniPrices = fetchLatestPricesByTicker(VNINDEX);
        if (vniPrices.isEmpty()) {
            log.info("latest vni is empty, try fetch recent days");
            return fetchAllPricesRecentDays();
        } else {
            var lastDay = vniPrices.get(vniPrices.size() - 1);
            var lastDayPrices = fetchAllPricesByDate(lastDay.getDate());
            if (lastDayPrices.isEmpty()) {
                log.info("last day prices is empty, try fetch priority prices and recent days");
                var priorityPrices = fetchLatestPriorityPrices();
                var recentDayPrices = fetchAllPricesRecentDays();
                return Stream.of(vniPrices, priorityPrices, recentDayPrices)
                        .flatMap(List::stream).toList();
            } else {
                log.info("fetch additional index prices");
                var hnxiPrices = fetchLatestPricesByTicker(HNX_INDEX);
                return Stream.of(vniPrices, hnxiPrices, lastDayPrices)
                        .flatMap(List::stream).toList();
            }
        }
    }

    private List<StockPriceDTO> fetchLatestPriorityPrices() {
        var allPrices = new ArrayList<StockPriceDTO>();
        try {
            for (var ticker : priorityTickers) {
                Thread.sleep(1000);
                var prices = fetchLatestPricesByTicker(ticker);
                if (prices.isEmpty()) {
                    return allPrices;
                } else {
                    allPrices.addAll(prices);
                }
            }
            return allPrices;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return allPrices;
        } catch (Exception e) {
            log.error("Error when fetch data from second datasource gclient", e);
            return allPrices;
        }
    }

    private List<StockPriceDTO> fetchAllPricesByDate(LocalDate date) {
        try {
            return secondDatasourceClient.getRawStockData(date, resFmtData)
                    .stream()
                    .map(this::convertToStockPriceDTO)
                    .toList();
        } catch (Exception e) {
            log.warn("Error when fetch prices by date", e);
            return List.of();
        }
    }

    private List<StockPriceDTO> fetchAllPricesRecentDays() {
        return fetchRawPricesRecentDays().stream()
                .map(this::convertToStockPriceDTO)
                .toList();
    }

    private List<String> fetchRawPricesRecentDays() {
        var recentDates = TimeUtil.getRecentWeekDays(4);
        LocalDate existDate = null;
        List<String> rawIndex = null;
        for (var date : recentDates) {
            try {
                rawIndex = secondDatasourceClient.getRawStockData(date, resFmtIndex);
                existDate = date;
                log.info("Found data on {} size {}", existDate, rawIndex.size());
                break;
            } catch (Exception e) {
                log.error("Error when fetch data from second datasource", e);
            }
        }
        if (rawIndex == null) {
            log.error("Can not fetch data from second datasource");
            return List.of();
        }
        try {
            var rawData = secondDatasourceClient.getRawStockData(existDate, resFmtData);
            return Stream.of(rawIndex, rawData).flatMap(List::stream).toList();
        } catch (IOException e) {
            log.error("Error when fetch data from second datasource", e);
            return rawIndex;
        }
    }

    private StockPriceDTO convertToStockPriceDTO(String rawPriceData) {
        var cols = rawPriceData.split(",");
        return StockPriceDTO.builder()
                .ticker(cols[0])
                .date(TimeUtil.parseDate(cols[1], "yyyyMMdd"))
                .open(Double.parseDouble(cols[2]))
                .high(Double.parseDouble(cols[3]))
                .low(Double.parseDouble(cols[4]))
                .close(Double.parseDouble(cols[5]))
                .volume(Double.parseDouble(cols[6]))
                .build();
    }

    private List<StockPriceDTO> fetchLatestPricesByTicker(String ticker) {
        try {
            return secondDatasourceGClient.fetchPricesByTicker(ticker);
        } catch (Exception e) {
            log.error("gclient fetch error {} {}", ticker, e.getLocalizedMessage());
            return List.of();
        }
    }
}
