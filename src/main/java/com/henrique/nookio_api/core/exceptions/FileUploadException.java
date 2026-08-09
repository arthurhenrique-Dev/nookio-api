package com.henrique.nookio_api.core.exceptions;

import org.springframework.http.HttpStatus;

public class FileUploadException extends NookioException {

    public FileUploadException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_UPLOAD_ERROR", message);
    }

    public FileUploadException(String message, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_UPLOAD_ERROR", message, cause);
    }
}
