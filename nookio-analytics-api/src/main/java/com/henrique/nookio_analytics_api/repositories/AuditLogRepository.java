package com.henrique.nookio_analytics_api.repositories;

import com.henrique.nookio_analytics_api.models.AuditLogsModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.time.LocalDateTime;

public interface AuditLogRepository extends ElasticsearchRepository<AuditLogsModel, String> {

    Page<AuditLogsModel> findByTimestampBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<AuditLogsModel> findByApiIdAndTimestampBetween(Integer apiId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<AuditLogsModel> findByOperationAndTimestampBetween(String operation, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<AuditLogsModel> findByApiIdAndOperationAndTimestampBetween(Integer apiId, String operation, LocalDateTime start, LocalDateTime end, Pageable pageable);
}
