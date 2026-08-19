package com.henrique.nookio_api.core.audit_logs.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Result {
    SUCCESS(0, 200, 299),
    FAILED(1, 400, 400),
    DENIED(2, 403, 403),
    UNAUTHORIZED(3, 401, 401),
    PENDING(4, 100, 199),
    TIMEOUT(5, 408, 408),
    ERROR(6, 500, 599);

    private final Integer code;
    private final int minHttpCode;
    private final int maxHttpCode;

    public Integer getCode() {
        return code;
    }
    public static Result fromHttpStatusCode(int statusCode) {
        for (Result result : values()) {
            if (statusCode >= result.minHttpCode && statusCode <= result.maxHttpCode) {
                return result;
            }
        }
        return ERROR; // fallback padrão
    }
}