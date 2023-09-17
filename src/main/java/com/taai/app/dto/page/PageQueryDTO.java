package com.taai.app.dto.page;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class PageQueryDTO {
    private Integer pageNumber;
    private Integer pageSize;
}
