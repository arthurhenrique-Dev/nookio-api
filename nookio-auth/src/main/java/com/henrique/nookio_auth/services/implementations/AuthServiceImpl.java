package com.henrique.nookio_auth.services.implementations;

import com.henrique.nookio_auth.dto.LoginRequestDto;
import com.henrique.nookio_auth.dto.RegisterRequestDto;
import com.henrique.nookio_auth.dto.TokenResponseDto;
import com.henrique.nookio_auth.repository.UserRepository;
import com.henrique.nookio_auth.services.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    @Override
    public TokenResponseDto login(LoginRequestDto request) {
        log.info("[AUTH_LOGIN_REQUEST] email={}", request.email());
        // TODO: Implement authentication logic by hand
        throw new UnsupportedOperationException("Login method to be implemented by developer");
    }

    @Override
    public TokenResponseDto register(RegisterRequestDto request) {
        log.info("[AUTH_REGISTER_REQUEST] email={}", request.email());
        // TODO: Implement registration logic by hand
        throw new UnsupportedOperationException("Register method to be implemented by developer");
    }

    @Override
    public boolean validateToken(String token) {
        log.info("[AUTH_VALIDATE_TOKEN]");
        // TODO: Implement token validation logic by hand
        return false;
    }
}
