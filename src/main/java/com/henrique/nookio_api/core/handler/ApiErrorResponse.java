package com.henrique.nookio_api.core.handler;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
        int status,
        String errorCode,
        String message,
        String path,
        Instant timestamp,
        String debugId,
        List<FieldErrorDetail> details
) {
    public static ApiErrorResponse of(int status, String errorCode, String message, String path, String debugId, List<FieldErrorDetail> details) {
        return new ApiErrorResponse(status, errorCode, message, path, Instant.now(), debugId, details);
    }

    public static ApiErrorResponse of(int status, String errorCode, String message, String path, String debugId) {
        return new ApiErrorResponse(status, errorCode, message, path, Instant.now(), debugId, null);
    }
}
