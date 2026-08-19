package com.henrique.nookio_api.core.audit_logs.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.LocalDateTime;

@Embeddable
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditLogData {

    private String ip;
    private String resource;
    private String operation;
    @Column(name = "result")
    private Integer result;
    @CurrentTimestamp
    private LocalDateTime timestamp;
}
