package com.henrique.nookio_api.modules.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UserEmailUpdateWebhookDto(
        @NotNull(message = "O ID do usuário é obrigatório.")
        Integer userId,

        @NotNull(message = "O novo e-mail é obrigatório.")
        @Email(message = "O formato do novo e-mail é inválido.")
        String newEmail
) {
}
