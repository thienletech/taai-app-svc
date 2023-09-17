package com.taai.app.dto.stock;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class StockPriceDTO {
    private String ticker;
    private LocalDate date;
    private Double open;
    private Double high;
    private Double low;
    private Double close;
    private Double volume;
}
