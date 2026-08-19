package com.henrique.nookio_api.modules.users.controllers;

import com.henrique.nookio_api.modules.users.dto.CreateUserDto;
import com.henrique.nookio_api.modules.users.dto.UserEmailUpdateWebhookDto;
import com.henrique.nookio_api.modules.users.dto.UserResponseDto;
import com.henrique.nookio_api.modules.users.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponseDto> createUser(@ModelAttribute @Valid CreateUserDto dto) {
        UserResponseDto response = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/webhook/email")
    public ResponseEntity<Void> updateEmailViaWebhook(@RequestBody @Valid UserEmailUpdateWebhookDto dto) {
        userService.updateEmailViaWebhook(dto);
        return ResponseEntity.ok().build();
    }
}
