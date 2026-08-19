package com.henrique.nookio_api.modules.properties.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdatePropertyDto(
        @NotNull Integer propertyId,
        String title,
        InformationUpdate informationUpdate,
        List<@Valid PhotoUpdatePayload> photos,
        Long swapPhotoId1,
        Long swapPhotoId2
) {
}

