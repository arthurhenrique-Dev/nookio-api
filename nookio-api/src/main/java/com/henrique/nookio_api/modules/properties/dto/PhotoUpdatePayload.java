package com.henrique.nookio_api.modules.properties.dto;

import com.henrique.nookio_api.modules.files.annotations.annotation.ValidFile;
import org.springframework.web.multipart.MultipartFile;

public record PhotoUpdatePayload(
        @ValidFile(allowedTypes = {"image/jpeg", "image/png"})
        MultipartFile file,
        Long photoId,
        Long order
) {
}
