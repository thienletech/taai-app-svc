package com.taai.app.controller.web;

import com.taai.app.dto.common.ResponsePayload;
import com.taai.app.dto.stock.StockDTO;
import com.taai.app.dto.stock.StockInfoDTO;
import com.taai.app.dto.stock.StockPredictionResultDTO;
import com.taai.app.service.stock.StockPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {
        RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.OPTIONS})
@RestController
@RequestMapping("/web")
public class WebController {
    @Autowired
    private StockPriceService stockPriceService;

    @GetMapping("/stocks/autocomplete")
    public ResponsePayload<List<StockDTO>> getAutocomplete(@RequestParam("query") String query) {
        var res = stockPriceService.getAutocomplete(query);
        return ResponsePayload.ok(res);
    }

    @GetMapping("/stock-prices/prediction")
    public ResponsePayload<StockPredictionResultDTO> getPrediction(@RequestParam("ticker") String ticker) {
        var res = stockPriceService.getStockPrediction(ticker);
        return ResponsePayload.ok(res);
    }

    @GetMapping("/stock-prices/info")
    public ResponsePayload<StockInfoDTO> getStockInfo(@RequestParam("ticker") String ticker) {
        var res = stockPriceService.getStockInfo(ticker);
        return ResponsePayload.ok(res);
    }
}
