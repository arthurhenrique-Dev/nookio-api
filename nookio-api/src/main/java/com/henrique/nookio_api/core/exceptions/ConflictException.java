package com.henrique.nookio_api.core.exceptions;

import org.springframework.http.HttpStatus;

public class ConflictException extends NookioException {

    public ConflictException(String errorCode, String message) {
        super(HttpStatus.CONFLICT, errorCode, message);
    }
}
