package com.henrique.nookio_api.infraestructure.microsservices.analytic;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "nookio-analytics-api")
public interface AnalyticsFeignClient {

    @PostMapping("/audit_logs")
    void sendAuditLogs(@RequestBody Map<String, Object> payload);
}
