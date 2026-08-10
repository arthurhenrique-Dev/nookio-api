package com.henrique.nookio_api.core.audit_logs.listener;

import com.henrique.nookio_api.core.audit_logs.event.AuditLogEvent;
import com.henrique.nookio_api.core.audit_logs.model.AuditLogData;
import com.henrique.nookio_api.core.audit_logs.service.orchestror.AuditOrchestror;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditLogEventListener {

    private final AuditOrchestror orchestror;

    @Async
    @EventListener
    public void handleAuditLogEvent(AuditLogEvent event) {
        AuditLogData data = (event.getData() != null)? event.getData() : null;
        if (data == null) return;
        log.debug("[AUDIT_EVENT_RECEIVED] Resource: {}, Operation: {}",
                data.getResource(),
                data.getOperation());
        orchestror.process(data);
    }
}
