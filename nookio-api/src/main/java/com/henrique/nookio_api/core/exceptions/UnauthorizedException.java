package com.henrique.nookio_api.core.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends NookioException {

    public UnauthorizedException(String errorCode, String message) {
        super(HttpStatus.UNAUTHORIZED, errorCode, message);
    }

    public UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", message);
    }
}
