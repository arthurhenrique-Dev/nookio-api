package com.henrique.nookio_auth.services;

import com.henrique.nookio_auth.dto.LoginRequestDto;
import com.henrique.nookio_auth.dto.RegisterRequestDto;
import com.henrique.nookio_auth.dto.TokenResponseDto;

public interface AuthService {
    TokenResponseDto login(LoginRequestDto request);
    TokenResponseDto register(RegisterRequestDto request);
    boolean validateToken(String token);
}
