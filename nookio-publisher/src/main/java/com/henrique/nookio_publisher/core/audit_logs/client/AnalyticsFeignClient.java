package com.henrique.nookio_publisher.core.audit_logs.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "NOOKIO-ANALYTICS-API")
public interface AnalyticsFeignClient {

    @PostMapping("/audit_logs")
    void sendAuditLogs(@RequestBody Map<String, Object> payload);
}
