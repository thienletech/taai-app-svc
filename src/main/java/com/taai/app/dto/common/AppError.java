package com.taai.app.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Data
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppError implements Serializable {
    @EqualsAndHashCode.Include
    private String code;
    private Integer status;
    private String message;
    private String detail;
    private String help;

    private static AppError of(String code, Integer status, String message) {
        return AppError.builder().code(code).status(status).message(message).build();
    }

    // standard
    public static final AppError OK = of("standard-1", HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase());
    public static final AppError CREATED = of("standard-2", HttpStatus.CREATED.value(), HttpStatus.CREATED.getReasonPhrase());
    public static final AppError BAD_REQUEST = of("standard-3", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase());
    public static final AppError UNAUTHORIZED = of("standard-4", HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
    public static final AppError FORBIDDEN = of("standard-5", HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase());
    public static final AppError NOT_FOUND = of("standard-6", HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase());
    public static final AppError INTERNAL_SERVER_ERROR = of("standard-7", HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());

}
