package com.henrique.nookio_analytics_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

public record AuditLogRequestDto(
        @JsonProperty("api_id") Integer apiId,
        List<AuditLogItemDto> logs
) {
    public record AuditLogItemDto(
            String ip,
            String resource,
            String operation,
            Integer result,
            LocalDateTime timestamp
    ) {}
}
