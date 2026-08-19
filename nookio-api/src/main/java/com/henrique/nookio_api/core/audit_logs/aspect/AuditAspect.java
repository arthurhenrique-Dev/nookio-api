package com.henrique.nookio_api.core.audit_logs.aspect;

import com.henrique.nookio_api.core.audit_logs.annotation.AuditLog;
import com.henrique.nookio_api.core.audit_logs.enums.Result;
import com.henrique.nookio_api.core.audit_logs.event.AuditLogEvent;
import com.henrique.nookio_api.core.audit_logs.model.AuditLogData;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final ApplicationEventPublisher eventPublisher;
    private final HttpServletRequest request;

    @Around("@annotation(auditLog)")
    public Object logAudit(ProceedingJoinPoint joinPoint, AuditLog auditLog) throws Throwable {
        String ip = request.getRemoteAddr();

        Object response = null;

        try {
            response = joinPoint.proceed();
        } finally {

            Result requestResult = Result
                    .fromHttpStatusCode(
                            extractStatusCode(response)
                    );
            AuditLogData logData = AuditLogData.builder()
                    .ip(ip)
                    .resource(auditLog.resource())
                    .operation(auditLog.operation())
                    .result(requestResult.getCode())
                    .build();

            eventPublisher.publishEvent(new AuditLogEvent(this, logData));
        }
            return response;
    }
    private int extractStatusCode(Object response){

        if(response instanceof ResponseEntity<?> responseEntity)
            return responseEntity
                    .getStatusCode()
                    .value();

        ServletRequestAttributes attr =
                (ServletRequestAttributes) RequestContextHolder
                        .getRequestAttributes();

        if (attr != null && attr.getResponse() != null)
            return attr
                    .getResponse()
                    .getStatus();
        return 500;
    }
}
