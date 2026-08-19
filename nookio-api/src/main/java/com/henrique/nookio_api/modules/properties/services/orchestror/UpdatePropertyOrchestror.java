package com.henrique.nookio_api.modules.properties.services.orchestror;

import com.henrique.nookio_api.core.exceptions.ResourceNotFoundException;
import com.henrique.nookio_api.modules.properties.dto.UpdatePropertyDto;
import com.henrique.nookio_api.modules.properties.models.Property;
import com.henrique.nookio_api.modules.properties.repository.PropertiesRepository;
import com.henrique.nookio_api.modules.properties.services.update.UpdatePropertyPhotosStrategy;
import com.henrique.nookio_api.modules.properties.services.update.UpdatePropertyInformationStrategy;
import com.henrique.nookio_api.shared.logging.LogContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdatePropertyOrchestror {

    private final PropertiesRepository propertiesRepository;
    private final UpdatePropertyInformationStrategy informationStrategy;
    private final UpdatePropertyPhotosStrategy photosStrategy;

    @Transactional
    public void execute(UpdatePropertyDto dto) {
        String debugId = LogContext.getDebugId();
        Property property = propertiesRepository.findById(dto.propertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property", dto.propertyId()));

        if (dto.title() != null && !dto.title().isBlank()) {
            property.setTitle(dto.title());
            propertiesRepository.save(property);
            log.info("[UPDATE_PROPERTY_TITLE] debugId={} propertyId={} newTitle={}", debugId, dto.propertyId(), dto.title());
        }

        if (dto.informationUpdate() != null) {
            log.info("[UPDATE_PROPERTY_INFO] debugId={} propertyId={}", debugId, dto.propertyId());
            informationStrategy.update(property, dto.informationUpdate());
        }
    }
}
