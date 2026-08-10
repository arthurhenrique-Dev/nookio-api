package com.henrique.nookio_publisher.core.audit_logs.event;

import java.time.LocalDateTime;

public record AuditLogEvent(
        String ip,
        String resource,
        String operation,
        Integer result,
        LocalDateTime timestamp
) {
}
