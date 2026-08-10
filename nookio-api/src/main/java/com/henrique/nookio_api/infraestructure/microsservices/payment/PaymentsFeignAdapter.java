package com.henrique.nookio_api.infraestructure.microsservices.payment;

import com.henrique.nookio_api.infraestructure.microsservices.payment.dto.PaymentResponseDto;
import com.henrique.nookio_api.infraestructure.microsservices.payment.dto.PaymentWebhookDto;
import com.henrique.nookio_api.modules.schedules.dto.PaymentRequestDto;
import com.henrique.nookio_api.modules.schedules.models.Schedule;
import com.henrique.nookio_api.modules.schedules.models.ScheduleStatus;
import com.henrique.nookio_api.modules.schedules.repository.ScheduleRepository;
import com.henrique.nookio_api.shared.logging.LogContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentsFeignAdapter implements PaymentsPort {

    private final PaymentsFeignClient paymentsFeignClient;
    private final ScheduleRepository scheduleRepository;

    @Override
    public PaymentResponseDto sendPayment(PaymentRequestDto request) {
        String debugId = LogContext.getDebugId();
        log.info("[PAYMENTS_SEND_REQUEST_STARTED] debugId={} endpoint=/payments", debugId);

        try {
            PaymentResponseDto response = paymentsFeignClient.sendPayment(request);
            log.info("[PAYMENTS_SEND_SUCCESS] debugId={}", debugId);
            return response;
        } catch (Exception e) {
            log.error("[PAYMENTS_SEND_ERROR] debugId={} error={}", debugId, e.getMessage());
            return new PaymentResponseDto(null, "FAILED", "Falha ao enviar pagamento para o microsserviço.");
        }
    }

    @Override
    public void responsePayment(PaymentWebhookDto webhook) {
        String debugId = LogContext.getDebugId();
        log.info("[PAYMENTS_RESPONSE_WEBHOOK] debugId={} scheduleId={} paymentId={} status={}",
                debugId, webhook.scheduleId(), webhook.paymentId(), webhook.status());

        if (webhook.scheduleId() == null) return;

        var scheduleOpt = scheduleRepository.findById(webhook.scheduleId());
        if (scheduleOpt.isEmpty()) {
            log.warn("[PAYMENTS_RESPONSE_SCHEDULE_NOT_FOUND] debugId={} scheduleId={}", debugId, webhook.scheduleId());
            return;
        }

        Schedule schedule = scheduleOpt.get();
        try {
            ScheduleStatus status = ScheduleStatus.fromString(webhook.status());
            schedule.setStatus(status != null ? status : ScheduleStatus.CANCELLED);
            if (webhook.paymentId() != null) schedule.setPaymentId(webhook.paymentId());
            scheduleRepository.save(schedule);
            log.info("[PAYMENTS_RESPONSE_SCHEDULE_UPDATED] debugId={} scheduleId={} newStatus={}",
                    debugId, schedule.getId(), schedule.getStatus());
        } catch (Exception e) {
            log.error("[PAYMENTS_RESPONSE_ERROR] debugId={} scheduleId={} error={}", debugId, webhook.scheduleId(), e.getMessage());
        }
    }

    @Override
    public void repay(List<UUID> payments) {
        String debugId = LogContext.getDebugId();
        log.info("[PAYMENTS_FEIGN_REPAY_STARTED] debugId={} count={}", debugId, payments != null ? payments.size() : 0);

        try {
            paymentsFeignClient.repay(payments);
            log.info("[PAYMENTS_FEIGN_REPAY_SUCCESS] debugId={}", debugId);
        } catch (Exception e) {
            log.error("[PAYMENTS_FEIGN_REPAY_FAILED] debugId={} error={}", debugId, e.getMessage());
        }
    }
}
