package com.henrique.nookio_api.modules.avaliations.services;

import com.henrique.nookio_api.core.exceptions.BusinessRuleException;
import com.henrique.nookio_api.core.exceptions.ConflictException;
import com.henrique.nookio_api.core.exceptions.ResourceNotFoundException;
import com.henrique.nookio_api.modules.avaliations.dto.CreateAvaliationDto;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvaliationsService {

    private final AvaliationRepository avaliationRepository;
    private final ScheduleRepository scheduleRepository;
    private final CatalogCacheInvalidator catalogCacheInvalidator;

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

        Avaliation avaliation = Avaliation.builder()
                .avaliatorId(schedule.getGuestId().longValue())
                .propertyId(schedule.getPropertyId().longValue())
                .ownerId(schedule.getOwnerId().longValue())
                .rating(dto.rating())
                .description(dto.description())
                .build();

        Avaliation savedAvaliation = avaliationRepository.save(avaliation);
        schedule.setAvaliation(savedAvaliation);
        scheduleRepository.save(schedule);
        log.info("[AVALIATE_SUCCESS] debugId={} scheduleId={} avaliationId={} rating={}",
                debugId, scheduleId, savedAvaliation.getId(), dto.rating());

        if (schedule.getPropertyId() != null) {
            catalogCacheInvalidator.invalidateAffectedCaches(schedule.getPropertyId());
        }

        return savedAvaliation;
    }

    @Cacheable(value = "avaliations", key = "#propertyId + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Slice<Avaliation> findByPropertyId(Long propertyId, Pageable pageable) {
        if (pageable == null) pageable = PageRequest.of(0, 10);
        return avaliationRepository.findAllByPropertyIdOrderByCreatedAtDesc(propertyId, pageable);
    }
}
