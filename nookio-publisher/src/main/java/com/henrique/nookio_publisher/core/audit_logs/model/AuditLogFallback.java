package com.henrique.nookio_publisher.core.audit_logs.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log_fallbacks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogFallback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ip;
    private String resource;
    private String operation;
    private Integer result;
    private LocalDateTime timestamp;
}
