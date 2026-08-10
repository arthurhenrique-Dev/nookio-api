package com.henrique.nookio_api.modules.users.dto;

public record UserResponseDto(
        Integer id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String cpf,
        Integer profileFileId
) {
}
