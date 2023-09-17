package com.taai.app.domain.stock;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "stock_price")
public class StockPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ticker;
    @Temporal(TemporalType.DATE)
    private LocalDate date;
    private Double open;
    private Double high;
    private Double low;
    private Double close;
    private Double volume;
}
