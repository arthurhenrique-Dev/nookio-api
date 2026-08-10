package com.henrique.nookio_api.core.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ValidationException extends NookioException {

    private final String field;

    public ValidationException(String field, String message) {
        super(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message);
        this.field = field;
    }

    public ValidationException(String errorCode, String field, String message) {
        super(HttpStatus.BAD_REQUEST, errorCode, message);
        this.field = field;
    }
}
