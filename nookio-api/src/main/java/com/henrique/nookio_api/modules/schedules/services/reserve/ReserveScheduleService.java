package com.henrique.nookio_api.modules.schedules.services.reserve;

import com.henrique.nookio_api.core.exceptions.ConflictException;
import com.henrique.nookio_api.modules.schedules.dto.ReserveScheduleDto;
import com.henrique.nookio_api.modules.schedules.models.Schedule;
import com.henrique.nookio_api.modules.schedules.repository.ScheduleRepository;
import com.henrique.nookio_api.shared.logging.LogContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class ReserveScheduleService {

    private final ScheduleRepository repository;

    public Schedule exec(ReserveScheduleDto context) {
        String debugId = LogContext.getDebugId();
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(15);

        boolean busy = repository.existsConflictingReservation(
                context.propertyId(), context.start(), context.end(), cutoffTime
        );

        if (busy) {
            log.warn("[RESERVE_CONFLICT] debugId={} propertyId={} start={} end={}", debugId, context.propertyId(), context.start(), context.end());
            throw new ConflictException("PROPERTY_UNAVAILABLE", "Imóvel não está disponível para o período selecionado.");
        }

        Schedule schedule = Schedule.builder()
                .propertyId(context.propertyId())
                .guestId(context.userId())
                .ownerId(context.ownerId())
                .start(context.start())
                .end(context.end())
                .reservatedAt(LocalDateTime.now())
                .build();

        Schedule saved = repository.save(schedule);
        log.info("[RESERVE_SAVED] debugId={} scheduleId={}", debugId, saved.getId());
        return saved;
    }
}
