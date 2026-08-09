package com.henrique.nookio_api.modules.schedules.services.reserve;

import com.henrique.nookio_api.core.exceptions.ExternalServiceException;
import com.henrique.nookio_api.infraestructure.microsservices.payment.PaymentsPort;
import com.henrique.nookio_api.infraestructure.microsservices.payment.dto.PaymentResponseDto;
import com.henrique.nookio_api.modules.schedules.dto.PaymentRequestDto;
import com.henrique.nookio_api.modules.schedules.models.Schedule;
import com.henrique.nookio_api.modules.schedules.models.ScheduleStatus;
import com.henrique.nookio_api.modules.schedules.repository.ScheduleRepository;
import com.henrique.nookio_api.shared.logging.LogContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class PayReserveService {

    private final PaymentsPort paymentsPort;
    private final ScheduleRepository repository;

    public void exec(PaymentRequestDto context, Schedule schedule) {
        String debugId = LogContext.getDebugId();
        log.info("[PAYMENT_PROCESSING_STARTED] debugId={} scheduleId={}", debugId, schedule.getId());

        PaymentResponseDto response = paymentsPort.processPayment(context);
        log.info("[PAYMENT_RESPONSE_RECEIVED] debugId={} status={} paymentId={}",
                debugId, response.status(), response.paymentId());

        Schedule updatedSchedule = processPaymentResponse(schedule, response);
        repository.save(updatedSchedule);

        if (updatedSchedule.getStatus() == ScheduleStatus.CANCELLED) {
            log.error("[PAYMENT_FAILED_CANCELLED] debugId={} scheduleId={} message={}",
                    debugId, schedule.getId(), response.message());
            throw new ExternalServiceException("PAYMENT_FAILED", "Falha no pagamento: " + response.message());
        }
    }

    private Schedule processPaymentResponse(Schedule schedule, PaymentResponseDto response) {
        String debugId = LogContext.getDebugId();
        try {
            ScheduleStatus status = ScheduleStatus.fromString(response.status());
            schedule.setStatus(status);
            schedule.setPaymentId(response.paymentId());
            log.info("[PAYMENT_STATUS_UPDATED] debugId={} scheduleId={} newStatus={}",
                    debugId, schedule.getId(), status);
        } catch (Exception e) {
            log.warn("[PAYMENT_STATUS_PARSE_ERROR] debugId={} error={} triggering refund for paymentId={}",
                    debugId, e.getMessage(), response.paymentId());
            schedule.setStatus(ScheduleStatus.CANCELLED);
            if (response.paymentId() != null) {
                log.info("[PAYMENT_REFUND_TRIGGERED] debugId={} paymentId={}", debugId, response.paymentId());
                paymentsPort.repay(List.of(response.paymentId()));
            }
        }
        return schedule;
    }
}