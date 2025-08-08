package com.ebang.openapi.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author chenlanqing 2025/7/4 13:21
 * @version 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OpenApiException extends RuntimeException {
    private static final long DEFAULT_ERROR_CODE = 404;

    private final long errorCode;
    private final String message;

    public OpenApiException(String message) {
        super(message);
        this.errorCode = DEFAULT_ERROR_CODE;
        this.message = message;
    }

    public OpenApiException(long errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public OpenApiException(OpenApiErrorCodeEnums openApiErrorCodeEnums) {
        super(openApiErrorCodeEnums.getMessage());
        this.errorCode = openApiErrorCodeEnums.getErrorCode();
        this.message = openApiErrorCodeEnums.getMessage();
    }
}
