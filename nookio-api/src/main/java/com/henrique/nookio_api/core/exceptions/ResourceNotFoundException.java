package com.henrique.nookio_api.core.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends NookioException {

    public ResourceNotFoundException(String errorCode, String message) {
        super(HttpStatus.NOT_FOUND, errorCode, message);
    }

    public ResourceNotFoundException(String resourceName, Object identifier) {
        super(HttpStatus.NOT_FOUND, resourceName.toUpperCase() + "_NOT_FOUND", resourceName + " não encontrado(a) para o identificador: " + identifier);
    }
}
