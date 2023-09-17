package com.taai.app.mapper.stock;

import com.taai.app.domain.stock.StockPrice;
import com.taai.app.dto.stock.StockInfoDTO;
import com.taai.app.dto.stock.StockPriceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@SuppressWarnings("unused")
@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StockPriceMapper {
    StockPriceMapper INSTANCE = Mappers.getMapper(StockPriceMapper.class);

    StockPriceDTO toDTO(StockPrice stockPrice);

    StockInfoDTO toStockInfoDTO(StockPrice stockPrice);

    StockPrice toEntity(StockPriceDTO stockPriceDTO);
}
