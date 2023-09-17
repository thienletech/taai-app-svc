package com.taai.app.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponsePayload<T> {
    private T data;
    private AppError error;

    public static <T> ResponsePayload<T> ok(T data) {
        return new ResponsePayload<>(data, AppError.OK);
    }

    public static <T> ResponsePayload<T> created(T data) {
        return new ResponsePayload<>(data, AppError.CREATED);
    }

    public static <T> ResponsePayload<T> error(AppError appError) {
        return new ResponsePayload<>(null, appError);
    }
}
