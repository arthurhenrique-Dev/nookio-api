package com.henrique.nookio_auth.dto;

public record TokenResponseDto(
        String accessToken,
        String tokenType,
        Long expiresIn
) {
    public static TokenResponseDto of(String token, Long expiresIn) {
        return new TokenResponseDto(token, "Bearer", expiresIn);
    }
}
