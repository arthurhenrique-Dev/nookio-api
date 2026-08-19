package com.henrique.nookio_api.modules.schedules.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import org.hibernate.validator.constraints.br.CPF;

@Builder
public record CustomerDetailsInfo(
        String fullname,
        String email,

        @NotBlank(message = "Tax ID is required")
        @CPF
        String taxId,

        @NotBlank(message = "Phone number is required")
        @Pattern(
                regexp = "^\\+?[1-9]\\d{1,14}$",
                message = "Phone number must follow an international format (e.g., +5511999999999) or contain only valid digits"
        )
        String phone
) {
}
