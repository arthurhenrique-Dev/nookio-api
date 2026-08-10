package com.henrique.nookio_publisher.core.audit_logs.aspect;

import com.henrique.nookio_publisher.core.audit_logs.annotation.AuditLog;
import com.henrique.nookio_publisher.core.audit_logs.event.AuditLogEvent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private final ApplicationEventPublisher eventPublisher;

    @Around("@annotation(auditLog)")
    public Object logAudit(ProceedingJoinPoint joinPoint, AuditLog auditLog) throws Throwable {
        int result = 200;
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            result = 500;
            throw e;
        } finally {
            String ip = getClientIp();
            AuditLogEvent event = new AuditLogEvent(
                    ip,
                    auditLog.resource(),
                    auditLog.operation(),
                    result,
                    LocalDateTime.now()
            );
            eventPublisher.publishEvent(event);
        }
    }

    private String getClientIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return "127.0.0.1";
        HttpServletRequest request = attributes.getRequest();
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = request.getRemoteAddr();
        return ip;
    }
}
