package com.henrique.nookio_api.modules.properties.services;

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
        String canonicalParams = catalogSearchCanonicalizer.serialize(input);
        eventPublisher.publishEvent(new CatalogSearchEvent(canonicalParams, input));

        Slice<VwPropertiesCatalog> cached = catalogCacheService.get(input);
        if (cached != null) return cached;

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



    public void createProperty(RegisterPropertyDto dto){
        Property created = createFacade.execute(dto);
        if (created != null && created.getId() != null) selectiveCacheInvalidator.invalidateAffectedCaches(created.getId());
    }

    public void updateProperty(UpdatePropertyDto dto){
        updateOrchestror.execute(dto);
        if (dto != null && dto.propertyId() != null) selectiveCacheInvalidator.invalidateAffectedCaches(dto.propertyId());
    }

    @Transactional
    public void deleteProperty(Integer propertyId, Integer ownerId){
        Property property = propertiesRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found."));

        if (!property.getOwnerId().equals(ownerId)) throw new IllegalArgumentException("Only the property owner can do it!");

        property.setActive(false);
        propertiesRepository.save(property);
        selectiveCacheInvalidator.invalidateAffectedCaches(propertyId);
    }
}
