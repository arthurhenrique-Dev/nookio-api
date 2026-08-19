package com.henrique.nookio_api.core.audit_logs.event;

import com.henrique.nookio_api.core.audit_logs.model.AuditLogData;
import lombok.Data;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
@Data
public class AuditLogEvent extends ApplicationEvent {

    private final AuditLogData data;

    public AuditLogEvent(Object source, AuditLogData data) {
        super(source);
        this.data = data;
    }
}
