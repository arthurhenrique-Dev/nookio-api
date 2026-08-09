package com.henrique.nookio_api.core.exceptions;

import org.springframework.http.HttpStatus;

public class ForbiddenOperationException extends NookioException {

    public ForbiddenOperationException(String errorCode, String message) {
        super(HttpStatus.FORBIDDEN, errorCode, message);
    }
}
