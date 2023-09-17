package com.taai.app.dto.stock;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class StockDTO {
    private String ticker;
    private String name;
    private String market;
}
