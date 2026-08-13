package com.henrique.nookio_api.core.audit_logs.service.strategies.implementations;

import com.henrique.nookio_api.core.audit_logs.model.AuditLogData;
import com.henrique.nookio_api.core.audit_logs.service.StompAuditLogPublisher;
import com.henrique.nookio_api.core.audit_logs.service.strategies.intefaces.AuditStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DirectLogDispatch implements AuditStrategy {

    private final StompAuditLogPublisher stompPublisher;

    @Override
    public void handle(AuditLogData data) {
        stompPublisher.sendAuditLog(data);
    }
}
