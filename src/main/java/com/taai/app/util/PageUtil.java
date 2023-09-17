package com.taai.app.util;

import com.taai.app.dto.page.PageDTO;
import com.taai.app.dto.page.PageQueryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@SuppressWarnings("unused")
public class PageUtil {
    private PageUtil() {
    }

    public static Pageable createPageRequest(PageQueryDTO request) {
        return createPageRequest(request.getPageNumber(), request.getPageSize());
    }

    public static Pageable createPageRequest(Integer page, Integer size) {
        if (page == null || size == null) {
            return PageRequest.of(AppConst.DEFAULT_PAGE_NUMBER - 1, AppConst.DEFAULT_PAGE_SIZE);
        } else {
            return PageRequest.of(page - 1, size);
        }
    }

    public static <T> PageDTO<T> toDTO(Page<T> page) {
        return PageDTO.of(page.getContent(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getNumber(),
                page.getNumberOfElements());
    }

    public static <S, D> PageDTO<D> toDTO(Page<S> page, Class<D> dstType) {
        return PageDTO.of(page.getContent().stream()
                        .map(item -> MapperUtil.map(item, dstType))
                        .toList(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getNumber(),
                page.getNumberOfElements());
    }

}
