package com.henrique.nookio_api.core.audit_logs.service.orchestror;

import com.henrique.nookio_api.core.audit_logs.model.AuditLogData;
import com.henrique.nookio_api.core.audit_logs.service.strategies.implementations.DatabaseLogDispatch;
import com.henrique.nookio_api.core.audit_logs.service.strategies.implementations.DirectLogDispatch;
import com.henrique.nookio_api.core.audit_logs.service.strategies.intefaces.AuditStrategy;
import com.henrique.nookio_api.core.health_monitor.ApplicationStress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditOrchestror {

    private final DatabaseLogDispatch dbDispatch;
    private final DirectLogDispatch directDispatch;
    private final ApplicationStress stress;

    public void process(AuditLogData data) {
        if (stress.isStressed()){
            dbDispatch.handle(data);
            return;
        }
        try {
            directDispatch.handle(data);
        } catch(Exception e){
            log.warn("Falha no envio HTTP do audit log. Salvando localmente via fallback... Erro: {}", e.getMessage());
            dbDispatch.handle(data);
        }
    }
}
