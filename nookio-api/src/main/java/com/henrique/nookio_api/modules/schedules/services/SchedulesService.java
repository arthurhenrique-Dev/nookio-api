package com.henrique.nookio_api.modules.schedules.services;

import com.henrique.nookio_api.core.exceptions.BusinessRuleException;
import com.henrique.nookio_api.core.exceptions.ResourceNotFoundException;
import com.henrique.nookio_api.infraestructure.microsservices.payment.PaymentsPort;
import com.henrique.nookio_api.modules.schedules.dto.ReserveScheduleDto;
import com.henrique.nookio_api.modules.schedules.models.Schedule;
import com.henrique.nookio_api.modules.schedules.models.ScheduleStatus;
import com.henrique.nookio_api.modules.schedules.repository.ScheduleRepository;
import com.henrique.nookio_api.modules.schedules.services.orchestror.ReserveFacade;
import com.henrique.nookio_api.shared.logging.LogContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulesService {

    private final ScheduleRepository repository;
    private final ReserveFacade reserveFacade;
    private final PaymentsPort paymentsPort;

    public void reserve(ReserveScheduleDto dto) {
        String debugId = LogContext.getDebugId();
        log.info("[RESERVE_SCHEDULE_STARTED] debugId={} propertyId={}", debugId, dto.propertyId());
        try {
            reserveFacade.execute(dto);
            log.info("[RESERVE_SCHEDULE_SUCCESS] debugId={} propertyId={}", debugId, dto.propertyId());
        } finally {
            LogContext.clear();
        }
    }

    @Transactional
    public void checkIn(Integer scheduleId) {
        String debugId = LogContext.getDebugId();
        Schedule schedule = loadSchedule(scheduleId);

        if (schedule.getStatus() != ScheduleStatus.CONFIRMED) {
            log.warn("[CHECKIN_FAILED] debugId={} scheduleId={} currentStatus={}", debugId, scheduleId, schedule.getStatus());
            throw new BusinessRuleException("INVALID_SCHEDULE_STATUS", "Apenas reservas confirmadas podem realizar check-in.");
        }

        if (schedule.getCheckIn() != null) {
            log.warn("[CHECKIN_FAILED] debugId={} scheduleId={} alreadyCheckedIn={}", debugId, scheduleId, schedule.getCheckIn());
            throw new BusinessRuleException("CHECKIN_ALREADY_DONE", "Check-in já foi realizado anteriormente.");
        }

        schedule.setCheckIn(LocalDateTime.now());
        repository.save(schedule);
        log.info("[CHECKIN_SUCCESS] debugId={} scheduleId={}", debugId, scheduleId);
    }

    @Transactional
    public void checkout(Integer scheduleId) {
        String debugId = LogContext.getDebugId();
        Schedule schedule = loadSchedule(scheduleId);

        if (schedule.getCheckIn() == null) {
            log.warn("[CHECKOUT_FAILED] debugId={} scheduleId={} checkInRequired=true", debugId, scheduleId);
            throw new BusinessRuleException("CHECKIN_REQUIRED", "Check-in é obrigatório antes de realizar o check-out.");
        }

        if (schedule.getCheckOut() != null) {
            log.warn("[CHECKOUT_FAILED] debugId={} scheduleId={} alreadyCheckedOut={}", debugId, scheduleId);
            throw new BusinessRuleException("CHECKOUT_ALREADY_DONE", "Check-out já foi realizado anteriormente.");
        }

        schedule.setCheckOut(LocalDateTime.now());
        schedule.setStatus(ScheduleStatus.COMPLETED);
        repository.save(schedule);
        log.info("[CHECKOUT_SUCCESS] debugId={} scheduleId={}", debugId, scheduleId);
    }

    @Transactional
    public void cancel(Integer scheduleId) {
        String debugId = LogContext.getDebugId();
        Schedule schedule = loadSchedule(scheduleId);

        schedule.setStatus(ScheduleStatus.CANCELLED);
        repository.save(schedule);
        log.info("[CANCEL_SCHEDULE_SUCCESS] debugId={} scheduleId={}", debugId, scheduleId);

        if (LocalDate.now().plusDays(3).isBefore(schedule.getStart())) {
            log.info("[CANCEL_REFUND_TRIGGERED] debugId={} paymentId={}", debugId, schedule.getPaymentId());
            paymentsPort.repay(List.of(schedule.getPaymentId()));
        }
    }

    private Schedule loadSchedule(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", id));
    }
}
