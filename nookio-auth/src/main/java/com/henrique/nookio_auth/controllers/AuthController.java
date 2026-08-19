package com.henrique.nookio_auth.controllers;

import com.henrique.nookio_auth.dto.LoginRequestDto;
import com.henrique.nookio_auth.dto.RegisterRequestDto;
import com.henrique.nookio_auth.dto.TokenResponseDto;
import com.henrique.nookio_auth.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        TokenResponseDto token = authService.login(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponseDto> register(@Valid @RequestBody RegisterRequestDto request) {
        TokenResponseDto token = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(token);
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestParam("token") String token) {
        boolean isValid = authService.validateToken(token);
        return ResponseEntity.ok(isValid);
    }
}
