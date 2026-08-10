package com.henrique.nookio_api.core.exceptions;

import org.springframework.http.HttpStatus;

public class BusinessRuleException extends NookioException {

    public BusinessRuleException(String errorCode, String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, errorCode, message);
    }
}
