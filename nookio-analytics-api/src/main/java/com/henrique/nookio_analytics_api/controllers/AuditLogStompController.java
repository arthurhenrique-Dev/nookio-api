package com.henrique.nookio_analytics_api.controllers;

import com.henrique.nookio_analytics_api.dto.AuditLogRequestDto;
import com.henrique.nookio_analytics_api.services.AnalyticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuditLogStompController {

    private final AnalyticService analyticService;

    @MessageMapping("/analytic-logs")
    public void receiveAuditLogMessage(@Payload AuditLogRequestDto payload) {
        log.info("[STOMP_AUDIT_LOG_RECEIVED] ApiId: {}", payload.apiId());
        analyticService.saveAuditLogs(payload);
    }
}
