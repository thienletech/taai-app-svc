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
@Table(name = "stock_algo")
public class StockAlgo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ticker;
    private Integer trainAuto;
    @Temporal(TemporalType.DATE)
    private LocalDate trainDate;

    private Integer predictAuto;
    @Temporal(TemporalType.DATE)
    private LocalDate predictDate;

    private Integer recommendAuto;
    @Temporal(TemporalType.DATE)
    private LocalDate recommendDate;
    private String recommendContent;

    private Integer priority;
}
