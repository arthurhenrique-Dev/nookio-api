package com.henrique.nookio_api.modules.avaliations.services;

import com.henrique.nookio_api.modules.avaliations.dto.CreateAvaliationDto;
import com.henrique.nookio_api.modules.avaliations.models.Avaliation;
import com.henrique.nookio_api.modules.avaliations.repository.AvaliationRepository;
import com.henrique.nookio_api.modules.schedules.models.Schedule;
import com.henrique.nookio_api.modules.schedules.models.ScheduleStatus;
import com.henrique.nookio_api.modules.schedules.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AvaliationsService {

    private final AvaliationRepository avaliationRepository;
    private final ScheduleRepository scheduleRepository;

    @Transactional
    public Avaliation avaliateSchedule(Integer scheduleId, CreateAvaliationDto dto) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva não encontrada."));

        if (schedule.getStatus() != ScheduleStatus.COMPLETED)
            throw new IllegalStateException("Apenas estadias concluídas (com check-out feito) podem ser avaliadas.");

        if (schedule.getAvaliation() != null)
            throw new IllegalStateException("Esta reserva já possui uma avaliação registrada.");

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

        return savedAvaliation;
    }

    @Cacheable(value = "avaliations", key = "#propertyId + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Slice<Avaliation> findByPropertyId(Long propertyId, Pageable pageable) {
        if (pageable == null) pageable = PageRequest.of(0, 10);
        return avaliationRepository.findAllByPropertyIdOrderByCreatedAtDesc(propertyId, pageable);
    }
}
