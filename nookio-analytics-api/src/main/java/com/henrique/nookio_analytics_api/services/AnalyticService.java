package com.henrique.nookio_analytics_api.services;

import com.henrique.nookio_analytics_api.dto.AuditLogRequestDto;
import com.henrique.nookio_analytics_api.models.AuditLogsModel;
import com.henrique.nookio_analytics_api.repositories.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticService {

    private final AuditLogRepository auditLogRepository;

    public void saveAuditLogs(AuditLogRequestDto request) {
        if (request == null || request.logs() == null || request.logs().isEmpty()) return;

        List<AuditLogsModel> models = request.logs().stream()
                .map(item -> AuditLogsModel.builder()
                        .apiId(request.apiId())
                        .ip(item.ip())
                        .resource(item.resource())
                        .operation(item.operation())
                        .result(item.result())
                        .timestamp(item.timestamp())
                        .build())
                .toList();

        auditLogRepository.saveAll(models);
        log.info("[ANALYTICS_LOGS_SAVED] apiId={} count={}", request.apiId(), models.size());
    }

    public Page<AuditLogsModel> searchAuditLogs(
            Integer apiId,
            String operation,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    ) {
        LocalDateTime start = startDate != null ? startDate : LocalDateTime.now().minusMonths(1);
        LocalDateTime end = endDate != null ? endDate : LocalDateTime.now();
        Pageable reqPageable = pageable != null ? pageable : PageRequest.of(0, 20);

        if (apiId != null && operation != null && !operation.isBlank()) return auditLogRepository.findByApiIdAndOperationAndTimestampBetween(apiId, operation.strip(), start, end, reqPageable);
        if (apiId != null) return auditLogRepository.findByApiIdAndTimestampBetween(apiId, start, end, reqPageable);
        if (operation != null && !operation.isBlank()) return auditLogRepository.findByOperationAndTimestampBetween(operation.strip(), start, end, reqPageable);

        return auditLogRepository.findByTimestampBetween(start, end, reqPageable);
    }
}
