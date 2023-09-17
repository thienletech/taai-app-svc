package com.taai.app.util.exception;

import com.taai.app.dto.common.AppError;
import lombok.Getter;

public class AppException extends RuntimeException {
    @Getter
    private final AppError appError;

    @SuppressWarnings("unused")
    public AppException(AppError appError, Throwable throwable) {
        super(throwable);
        this.appError = appError;
    }

    public AppException(AppError appError) {
        super(appError.getMessage());
        this.appError = appError;
    }
}
