package com.henrique.nookio_api.infraestructure.microsservices.payment;

import com.henrique.nookio_api.infraestructure.microsservices.payment.dto.PaymentResponseDto;
import com.henrique.nookio_api.modules.schedules.dto.PaymentRequestDto;
import com.henrique.nookio_api.shared.external_communication.BaseClient;
import com.henrique.nookio_api.shared.logging.LogContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@ConditionalOnProperty(
        prefix = "clients.payments",
        name = "transport",
        havingValue = "http",
        matchIfMissing = true
)
public class PaymentsHttpAdapter extends BaseClient implements PaymentsPort {

    public PaymentsHttpAdapter(
            @Value("${clients.api-id:1}") Integer apiId,
            @Value("${clients.payments.url}") String serviceUrl
    ) {
        super(apiId, serviceUrl);
    }

    @Override
    public PaymentResponseDto processPayment(PaymentRequestDto request) {
        String debugId = LogContext.getDebugId();
        log.info("[PAYMENTS_HTTP_REQUEST_STARTED] debugId={} endpoint=/payments", debugId);

        try {
            Map<HttpStatusCode, Object> response = request(HttpMethod.POST, "/payments", request);

            if (response != null && !response.isEmpty()) {
                HttpStatusCode statusCode = response.keySet().iterator().next();
                if (statusCode.is2xxSuccessful()) {
                    UUID generatedPaymentId = UUID.randomUUID();
                    log.info("[PAYMENTS_HTTP_SUCCESS] debugId={} statusCode={} paymentId={}",
                            debugId, statusCode, generatedPaymentId);
                    return new PaymentResponseDto(generatedPaymentId, "APPROVED", "Pagamento aprovado com sucesso.");
                }
            }
        } catch (Exception e) {
            log.error("[PAYMENTS_HTTP_ERROR] debugId={} error={}", debugId, e.getMessage());
        }

        log.warn("[PAYMENTS_HTTP_FAILED] debugId={} status=FAILED", debugId);
        return new PaymentResponseDto(null, "FAILED", "Falha ao processar pagamento no microsserviço.");
    }

    @Override
    public void repay(List<UUID> request) {
        String debugId = LogContext.getDebugId();
        log.info("[PAYMENTS_HTTP_REPAY_STARTED] debugId={} count={}", debugId, request != null ? request.size() : 0);

        try {
            request(HttpMethod.DELETE, "/payments", request);
            log.info("[PAYMENTS_HTTP_REPAY_SUCCESS] debugId={}", debugId);
        } catch (Exception e) {
            log.error("[PAYMENTS_HTTP_REPAY_FAILED] debugId={} error={}", debugId, e.getMessage());
        }
    }
}
