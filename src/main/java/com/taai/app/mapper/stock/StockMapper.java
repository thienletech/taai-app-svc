package com.taai.app.mapper.stock;

import com.taai.app.domain.stock.Stock;
import com.taai.app.dto.stock.StockDTO;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@SuppressWarnings("unused")
@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StockMapper {
    StockMapper INSTANCE = Mappers.getMapper(StockMapper.class);

    StockDTO toDTO(Stock stock);

    Stock toEntity(StockDTO stockDTO);
}
