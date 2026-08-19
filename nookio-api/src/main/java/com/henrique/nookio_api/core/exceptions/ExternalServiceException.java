package com.henrique.nookio_api.core.exceptions;

import org.springframework.http.HttpStatus;

public class ExternalServiceException extends NookioException {

    public ExternalServiceException(String errorCode, String message) {
        super(HttpStatus.BAD_GATEWAY, errorCode, message);
    }

    public ExternalServiceException(String errorCode, String message, Throwable cause) {
        super(HttpStatus.BAD_GATEWAY, errorCode, message, cause);
    }
}
