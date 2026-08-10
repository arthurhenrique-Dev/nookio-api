package com.henrique.nookio_api.core.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class NookioException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    public NookioException(HttpStatus status, String errorCode, String message) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public NookioException(HttpStatus status, String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.errorCode = errorCode;
    }
}
