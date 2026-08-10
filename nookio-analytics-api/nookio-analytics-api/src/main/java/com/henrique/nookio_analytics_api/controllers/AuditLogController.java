package com.henrique.nookio_analytics_api.controllers;

import com.henrique.nookio_analytics_api.dto.AuditLogRequestDto;
import com.henrique.nookio_analytics_api.models.AuditLogsModel;
import com.henrique.nookio_analytics_api.services.AnalyticService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/audit_logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AnalyticService analyticService;

    @PostMapping
    public ResponseEntity<Void> receiveAuditLogs(@RequestBody AuditLogRequestDto payload) {
        analyticService.saveAuditLogs(payload);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<AuditLogsModel>> searchAuditLogs(
            @RequestParam(required = false) Integer apiId,
            @RequestParam(required = false) String operation,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable
    ) {
        Page<AuditLogsModel> logs = analyticService.searchAuditLogs(apiId, operation, startDate, endDate, pageable);
        return ResponseEntity.ok(logs);
    }
}
