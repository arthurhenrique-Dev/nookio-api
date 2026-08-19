package com.henrique.nookio_api.modules.schedules.services.reserve;

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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class PayReserveService {

    private final PaymentsPort paymentsPort;
    private final ScheduleRepository repository;

    @Async
    public void exec(PaymentRequestDto context, Schedule schedule) {
        String debugId = LogContext.getDebugId();
        log.info("[PAYMENT_SEND_STARTED] debugId={} scheduleId={}", debugId, schedule.getId());

        PaymentResponseDto response = paymentsPort.sendPayment(context);
        log.info("[PAYMENT_SEND_DISPATCHED] debugId={} status={} paymentId={}",
                debugId, response.status(), response.paymentId());

        if ("FAILED".equalsIgnoreCase(response.status())) {
            log.error("[PAYMENT_SEND_FAILED] debugId={} scheduleId={} message={}",
                    debugId, schedule.getId(), response.message());
            schedule.setStatus(ScheduleStatus.CANCELLED);
            repository.save(schedule);
        }
    }
}