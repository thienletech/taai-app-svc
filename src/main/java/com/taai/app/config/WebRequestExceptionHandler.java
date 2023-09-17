package com.taai.app.config;

import com.taai.app.dto.common.AppError;
import com.taai.app.dto.common.ResponsePayload;
import com.taai.app.util.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice
public class WebRequestExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponsePayload<ResponsePayload<AppError>> handleUnknownException(Exception exception, WebRequest request) {
        log.error("Unknown exception in request {} : ", request, exception);
        return ResponsePayload.error(AppError.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ResponsePayload<AppError>> handleAppException(AppException exception, WebRequest request) {
        log.error("App exception in request {} : {}", request, exception);
        return ResponseEntity.status(exception.getAppError().getStatus())
                .body(ResponsePayload.error(exception.getAppError()));
    }
}
