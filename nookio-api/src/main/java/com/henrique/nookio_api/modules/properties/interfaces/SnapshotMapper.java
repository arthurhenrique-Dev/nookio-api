package com.henrique.nookio_api.modules.properties.interfaces;

import com.henrique.nookio_api.modules.location.models.Location;
import com.henrique.nookio_api.modules.properties.dto.SnapshotProperty;
import com.henrique.nookio_api.modules.properties.models.Property;
import com.henrique.nookio_api.modules.properties.models.PropertyInformation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SnapshotMapper {

    @Mapping(source = "location", target = "locationInformation")
    Location toLocation(SnapshotProperty snapshotProperty);

    @Mapping(source = "details", target = "propertyInformationDetails")
    PropertyInformation toInformation(SnapshotProperty snapshotProperty);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "snapshot.title", target = "title")
    @Mapping(source = "snapshot.ownerId", target = "ownerId")
    @Mapping(source = "informationId", target = "informationId")
    @Mapping(source = "locationId", target = "locationId")
    @Mapping(target = "active", constant = "true")
    Property toProperty(SnapshotProperty snapshot, Integer informationId, Integer locationId);
}

