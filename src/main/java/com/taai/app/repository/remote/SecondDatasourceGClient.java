package com.taai.app.repository.remote;

import com.nimbusds.jose.shaded.gson.Gson;
import com.taai.app.dto.stock.StockPriceDTO;
import com.taai.app.util.TimeUtil;
import lombok.Getter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FeignClient(name = "second-datasource-gclient", url = "${remote.second-datasource.graphql-url}")
@Component
public interface SecondDatasourceGClient {
    String REQUEST_PRICES_FMT = "{ \"query\": \"query {\\n                                  tradingViewData(symbol:\\\"%s\\\", from: \\\"%s\\\",to:\\\"%s\\\") {\\n                                   symbol\\n                                   open\\n                                   close\\n                                   high\\n                                   low\\n                                   volume\\n                                   time\\n                                  }\\n                                  dataTradingView15pAnd1h(symbol: \\\"%s\\\", type:  P1 ) {\\n                                    time\\n                                    symbol\\n                                    open\\n                                    close\\n                                    high\\n                                    low\\n                                    volume\\n                                }\\n                                  isTradeTime\\n                                   }\"}";

    Gson gson = new Gson();

    @PostMapping(value = "/graphql", consumes = MediaType.APPLICATION_JSON_VALUE)
    String post(@RequestBody String requestBody, @RequestHeader Map<String, String> headers);

    default List<StockPriceDTO> fetchPricesByTicker(String ticker) {
        var toDate = TimeUtil.formatDate(LocalDate.now(), "YYYY-MM-dd");
        var req = REQUEST_PRICES_FMT.formatted(ticker, "2001-01-01", toDate, ticker);
        var headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept-Language", "en-US,en;q=0.9");
        headers.put("Accept", "*/*");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Origin", "http://s.cafef.vn");
        headers.put("Referer", "http://s.cafef.vn/");
        headers.put("Sec-Ch-Ua", "\"Google Chrome\";v=\"113\", \"Chromium\";v=\"113\", \"Not-A.Brand\";v=\"24\"");
        headers.put("Sec-Ch-Ua-Platform", "\"Linux\"");
        headers.put("Sec-Fetch-Dest", "empty");
        headers.put("Sec-Fetch-Mode", "cors");
        headers.put("Sec-Fetch-Site", "cross-site");
        headers.put("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/000000000 Safari/537.36");
        var resp = post(req, headers);
        var responseDTO = gson.fromJson(resp, ResponseDTO.class);
        return responseDTO.data.getTradingViewData().stream().map(sochlv -> {
            var instant = Instant.ofEpochSecond(sochlv.getTime());
            var localDate = instant.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDate();
            return StockPriceDTO.builder()
                    .ticker(sochlv.getSymbol())
                    .date(localDate)
                    .open(sochlv.getOpen())
                    .high(sochlv.getHigh())
                    .low(sochlv.getLow())
                    .close(sochlv.getClose())
                    .volume(sochlv.getVolume())
                    .build();
        }).toList();
    }

    @Getter
    class ResponseDTO {
        ResponseDataDTO data;
    }

    @Getter
    @SuppressWarnings("unused")
    class ResponseDataDTO {
        private List<SOCHLV> tradingViewData;
    }

    @Getter
    @SuppressWarnings("unused")
    class SOCHLV {
        private String symbol;
        private long time;
        private double open;
        private double close;
        private double high;
        private double low;
        private double volume;
    }
}
