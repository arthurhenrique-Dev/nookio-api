package com.henrique.nookio_api.modules.properties.dto;

import com.henrique.nookio_api.modules.files.annotations.annotation.ValidFile;
import com.henrique.nookio_api.modules.location.dto.LocationInput;
import com.henrique.nookio_api.modules.properties.models.PropertyInformationDetails;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@NotNull

public record RegisterPropertyDto(


        @NotBlank String title,
        @NotNull Integer ownerId,
        PropertyInformationDetails propertyInformationDetails,
        LocationInput locationInput,
        @NotEmpty
        List<
                @ValidFile(
                        allowedTypes = {
                                "image/jpeg",
                                "image/png"
                        }
                )
                        MultipartFile
                > photos,
        @ValidFile
        MultipartFile ownerDocument,
        @ValidFile
        MultipartFile propertyDocument
) {
}
