package com.henrique.nookio_api.modules.properties.services;

import com.henrique.nookio_api.core.exceptions.ForbiddenOperationException;
import com.henrique.nookio_api.core.exceptions.ResourceNotFoundException;
import com.henrique.nookio_api.modules.properties.dto.CatalogationParameters;
import com.henrique.nookio_api.modules.properties.dto.InputCatalog;
import com.henrique.nookio_api.modules.properties.dto.RegisterPropertyDto;
import com.henrique.nookio_api.modules.properties.dto.UpdatePropertyDto;
import com.henrique.nookio_api.modules.properties.events.CatalogSearchEvent;
import com.henrique.nookio_api.modules.properties.interfaces.CatalogMapper;
import com.henrique.nookio_api.modules.properties.models.Property;
import com.henrique.nookio_api.modules.properties.models.VwPropertiesCatalog;
import com.henrique.nookio_api.modules.properties.repository.PropertiesRepository;
import com.henrique.nookio_api.modules.properties.repository.VwPropertiesCatalogRepository;
import com.henrique.nookio_api.modules.properties.repository.VwPropertiesCatalogSpecs;
import com.henrique.nookio_api.modules.properties.services.cache.CatalogCacheService;
import com.henrique.nookio_api.modules.properties.services.cache.CatalogCacheInvalidator;
import com.henrique.nookio_api.modules.properties.services.cache.canonicalizer.CatalogSearchCanonicalizer;
import com.henrique.nookio_api.modules.properties.services.facade.CreatePropertyFacade;
import com.henrique.nookio_api.modules.properties.services.orchestror.UpdatePropertyOrchestror;
import com.henrique.nookio_api.shared.logging.LogContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PropertiesService {

    private final VwPropertiesCatalogRepository vwPropertiesCatalogRepository;
    private final PropertiesRepository propertiesRepository;
    private final CreatePropertyFacade createFacade;
    private final UpdatePropertyOrchestror updateOrchestror;
    private final CatalogMapper catalogMapper;
    private final CatalogCacheService catalogCacheService;
    private final CatalogSearchCanonicalizer catalogSearchCanonicalizer;
    private final CatalogCacheInvalidator selectiveCacheInvalidator;
    private final ApplicationEventPublisher eventPublisher;

    public Slice<VwPropertiesCatalog> getCatalog(InputCatalog input) {
        String debugId = LogContext.getDebugId();
        String canonicalParams = catalogSearchCanonicalizer.serialize(input);
        eventPublisher.publishEvent(new CatalogSearchEvent(canonicalParams, input));

        Slice<VwPropertiesCatalog> cached = catalogCacheService.get(input);
        if (cached != null) {
            log.info("[CATALOG_CACHE_HIT] debugId={} canonicalParams={}", debugId, canonicalParams);
            return cached;
        }

        log.info("[CATALOG_CACHE_MISS] debugId={} queryDb=true", debugId);
        CatalogationParameters parameters = catalogMapper.toParameters(input);
        Specification<VwPropertiesCatalog> spec = VwPropertiesCatalogSpecs.filteredCatalog(parameters);

        Pageable pageable = (parameters != null && parameters.getInputPreSet() != null)
                ? parameters.getInputPreSet().pageable(
                        Sort.Order.desc("avaliation"),
                        Sort.Order.desc("totalSchedules")
                  )
                : VwPropertiesCatalogSpecs.pageable(null);

        Slice<VwPropertiesCatalog> result = vwPropertiesCatalogRepository.findAllBy(spec, pageable);
        catalogCacheService.put(input, result);
        return result;
    }

    public void warmupCatalog(InputCatalog input) {
        CatalogationParameters parameters = catalogMapper.toParameters(input);
        Specification<VwPropertiesCatalog> spec = VwPropertiesCatalogSpecs.filteredCatalog(parameters);

        Pageable pageable = (parameters != null && parameters.getInputPreSet() != null)
                ? parameters.getInputPreSet().pageable(
                        Sort.Order.desc("avaliation"),
                        Sort.Order.desc("totalSchedules")
                  )
                : VwPropertiesCatalogSpecs.pageable(null);

        Slice<VwPropertiesCatalog> result = vwPropertiesCatalogRepository.findAllBy(spec, pageable);
        catalogCacheService.putWarmup(input, result);
    }

    public void createProperty(RegisterPropertyDto dto) {
        String debugId = LogContext.getDebugId();
        log.info("[CREATE_PROPERTY] debugId={} title={}", debugId, dto.title());
        Property created = createFacade.execute(dto);
        if (created != null && created.getId() != null) {
            selectiveCacheInvalidator.invalidateAffectedCaches(created.getId());
        }
    }

    public void updateProperty(UpdatePropertyDto dto) {
        String debugId = LogContext.getDebugId();
        log.info("[UPDATE_PROPERTY] debugId={} propertyId={}", debugId, dto.propertyId());
        updateOrchestror.execute(dto);
        if (dto != null && dto.propertyId() != null) {
            selectiveCacheInvalidator.invalidateAffectedCaches(dto.propertyId());
        }
    }

    @Transactional
    public void deleteProperty(Integer propertyId, Integer ownerId) {
        String debugId = LogContext.getDebugId();
        Property property = propertiesRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property", propertyId));

        if (!property.getOwnerId().equals(ownerId)) {
            log.warn("[DELETE_PROPERTY_FORBIDDEN] debugId={} propertyId={} ownerId={}", debugId, propertyId, ownerId);
            throw new ForbiddenOperationException("OWNER_MISMATCH", "Apenas o proprietário do imóvel pode realizar esta exclusão.");
        }

        property.setActive(false);
        propertiesRepository.save(property);
        log.info("[DELETE_PROPERTY_SUCCESS] debugId={} propertyId={}", debugId, propertyId);

        selectiveCacheInvalidator.invalidateAffectedCaches(propertyId);
    }
}
