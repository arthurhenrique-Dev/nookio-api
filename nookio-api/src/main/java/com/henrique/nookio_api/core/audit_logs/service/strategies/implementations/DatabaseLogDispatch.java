package com.henrique.nookio_api.core.audit_logs.service.strategies.implementations;

import com.henrique.nookio_api.core.audit_logs.model.AuditLogData;
import com.henrique.nookio_api.core.audit_logs.model.AuditLogEntity;
import com.henrique.nookio_api.core.audit_logs.repository.AuditLogFallbackRepository;
import com.henrique.nookio_api.core.audit_logs.service.strategies.intefaces.AuditStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseLogDispatch implements AuditStrategy {

    private final AuditLogFallbackRepository fallbackRepository;

    @Override
    public void handle(AuditLogData data) {
        log.warn("Analytics service unavailable or high memory detected. Saving audit log to local JPA database...");
        AuditLogEntity entity = AuditLogEntity.builder()
                .auditLogData(data)
                .build();
        fallbackRepository.save(entity);
    }
}
