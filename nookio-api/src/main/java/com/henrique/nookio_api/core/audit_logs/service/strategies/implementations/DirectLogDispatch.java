package com.henrique.nookio_api.core.audit_logs.service.strategies.implementations;

import com.henrique.nookio_api.core.audit_logs.model.AuditLogData;
import com.henrique.nookio_api.infraestructure.microsservices.analytic.AnalyticsPort;
import com.henrique.nookio_api.core.audit_logs.service.strategies.intefaces.AuditStrategy;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Builder
@Component
@RequiredArgsConstructor
public class DirectLogDispatch implements AuditStrategy {

    private final AnalyticsPort analyticsPort;

    @Override
    public void handle(AuditLogData data) {
        analyticsPort.sendAuditLogs(data);
    }
}
