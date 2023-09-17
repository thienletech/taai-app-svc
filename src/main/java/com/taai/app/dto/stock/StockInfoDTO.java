package com.taai.app.dto.stock;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class StockInfoDTO {
    // stock prices
    private String ticker;
    private LocalDate date;
    private Double open;
    private Double high;
    private Double low;
    private Double close;
    private Double volume;

    // stock info summary
    private Double high52w;
    private Double low52w;
    private Double volume52w;
    private Double totalVolume52w;
    private Double totalCount52w;
    private Double ceiling;
    private Double floor;
    private Double reference;

    // stock recommendation
    private String recommendContent;
    private LocalDate recommendDate;
    private LocalDate predictDate;
}
