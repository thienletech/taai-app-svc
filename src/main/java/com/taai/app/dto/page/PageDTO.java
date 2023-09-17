package com.taai.app.dto.page;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor(staticName = "of")
public class PageDTO<T> {
    private List<T> items;
    private int totalPages;
    private long totalElements;
    private int pageNumber;
    private int pageSize;
}
