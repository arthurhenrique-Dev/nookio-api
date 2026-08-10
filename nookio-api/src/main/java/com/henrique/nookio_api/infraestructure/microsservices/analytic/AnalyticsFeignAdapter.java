package com.henrique.nookio_api.infraestructure.microsservices.analytic;

import com.henrique.nookio_api.core.audit_logs.model.AuditLogData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Component
public class AnalyticsFeignAdapter implements AnalyticsPort {

    private final AnalyticsFeignClient analyticsFeignClient;
    private final Integer apiId;

    public AnalyticsFeignAdapter(
            AnalyticsFeignClient analyticsFeignClient,
            @Value("${clients.api-id}") Integer apiId
    ) {
        this.analyticsFeignClient = analyticsFeignClient;
        this.apiId = apiId;
    }

    @Override
    public boolean sendAuditLogs(Collection<AuditLogData> dataList) {
        try {
            analyticsFeignClient.sendAuditLogs(Map.of(
                    "api_id", apiId,
                    "logs", dataList
            ));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
