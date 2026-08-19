package com.henrique.nookio_api.infraestructure.microsservices.analytic;

import com.henrique.nookio_api.core.audit_logs.model.AuditLogData;

import java.util.Collection;
import java.util.List;

public interface AnalyticsPort {

    default boolean sendAuditLogs(AuditLogData data) {
        return sendAuditLogs(List.of(data));
    }

    boolean sendAuditLogs(Collection<AuditLogData> dataList);
}
