package com.henrique.nookio_publisher.core.audit_logs.repository;

import com.henrique.nookio_publisher.core.audit_logs.model.AuditLogFallback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogFallbackRepository extends JpaRepository<AuditLogFallback, Long> {
}
