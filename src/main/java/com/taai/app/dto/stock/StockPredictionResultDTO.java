package com.taai.app.dto.stock;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class StockPredictionResultDTO {
    private List<StockPriceDTO> prices;
    private Integer predictionOffset;
}
