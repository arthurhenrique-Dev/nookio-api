package com.henrique.nookio_api.modules.users.dto;

import com.henrique.nookio_api.modules.files.annotations.annotation.ValidFile;
import org.springframework.web.multipart.MultipartFile;

public record CreateUserDto(
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String cpf,
        @ValidFile(allowedTypes = {"image/jpeg", "image/png"})
        MultipartFile profileFile
) {
}
