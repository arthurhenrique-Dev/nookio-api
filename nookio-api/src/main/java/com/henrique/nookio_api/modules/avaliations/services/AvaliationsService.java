package com.henrique.nookio_api.modules.avaliations.services;

import com.henrique.nookio_api.core.exceptions.BusinessRuleException;
import com.henrique.nookio_api.core.exceptions.ConflictException;
import com.henrique.nookio_api.core.exceptions.ResourceNotFoundException;
import com.henrique.nookio_api.modules.avaliations.dto.CreateAvaliationDto;
import com.henrique.nookio_api.modules.avaliations.event.AvaliationCreatedEvent;
import com.henrique.nookio_api.modules.avaliations.models.Avaliation;
import com.henrique.nookio_api.modules.avaliations.repository.AvaliationRepository;
import com.henrique.nookio_api.modules.properties.services.cache.CatalogCacheInvalidator;
import com.henrique.nookio_api.modules.schedules.models.Schedule;
import com.henrique.nookio_api.modules.schedules.models.ScheduleStatus;
import com.henrique.nookio_api.modules.schedules.repository.ScheduleRepository;
import com.henrique.nookio_api.shared.logging.LogContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvaliationsService {

    private final AvaliationRepository repository;
    private final ScheduleRepository scheduleRepository;
    private final CatalogCacheInvalidator catalogCacheInvalidator;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @CacheEvict(value = "avaliations", allEntries = true)
    public Avaliation avaliateSchedule(Integer scheduleId, CreateAvaliationDto dto) {
        String debugId = LogContext.getDebugId();
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", scheduleId));

        if (schedule.getStatus() != ScheduleStatus.COMPLETED) {
            log.warn("[AVALIATE_FAILED] debugId={} scheduleId={} currentStatus={}", debugId, scheduleId, schedule.getStatus());
            throw new BusinessRuleException("SCHEDULE_NOT_COMPLETED", "Apenas estadias concluídas (com check-out feito) podem ser avaliadas.");
        }

        if (schedule.getAvaliation() != null) {
            log.warn("[AVALIATE_FAILED] debugId={} scheduleId={} avaliationExists=true", debugId, scheduleId);
            throw new ConflictException("AVALIATION_ALREADY_EXISTS", "Esta reserva já possui uma avaliação registrada.");
        }

        Integer guestId = schedule.getGuestId();
        Integer propertyId = schedule.getPropertyId();
        Integer ownerId = schedule.getOwnerId();

        Avaliation avaliation = Avaliation.builder()
                .avaliatorId(guestId)
                .propertyId(propertyId)
                .ownerId(ownerId)
                .rating(dto.rating())
                .description(dto.description())
                .build();

        Avaliation savedAvaliation = repository.save(avaliation);
        schedule.setAvaliation(savedAvaliation);
        scheduleRepository.save(schedule);

        Integer avaliationId = savedAvaliation.getId();
        BigDecimal rating = savedAvaliation.getRating();
        String description = savedAvaliation.getDescription();

        log.info("[AVALIATE_SUCCESS] debugId={} scheduleId={} avaliationId={} rating={}",
                debugId, scheduleId, avaliationId, rating);

        if (propertyId != null) catalogCacheInvalidator.invalidateAffectedCaches(propertyId);

        eventPublisher.publishEvent(new AvaliationCreatedEvent(
                ownerId,
                propertyId,
                avaliationId,
                guestId,
                rating,
                description
        ));

        return savedAvaliation;
    }

    @Cacheable(value = "avaliations", key = "#propertyId + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Slice<Avaliation> findByPropertyId(Integer propertyId, Pageable pageable) {
        if (pageable == null) pageable = PageRequest.of(0, 10);
        return repository.findAllByPropertyIdOrderByCreatedAtDesc(propertyId, pageable);
    }
}