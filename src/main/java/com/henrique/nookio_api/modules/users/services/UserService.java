package com.henrique.nookio_api.modules.users.services;

import com.henrique.nookio_api.modules.files.models.File;
import com.henrique.nookio_api.modules.files.services.FileService;
import com.henrique.nookio_api.modules.users.dto.CreateUserDto;
import com.henrique.nookio_api.modules.users.dto.UserResponseDto;
import com.henrique.nookio_api.modules.users.models.User;
import com.henrique.nookio_api.modules.users.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FileService fileService;

    @Transactional
    public UserResponseDto createUser(CreateUserDto dto) {
        log.info("[CREATE_USER_REQUEST] email={}", dto.email());

        Integer profileFileId = null;
        if (dto.profileFile() != null && !dto.profileFile().isEmpty()) {
            List<File> uploadedFiles = fileService.upload(List.of(dto.profileFile()));
            if (uploadedFiles != null && !uploadedFiles.isEmpty()) profileFileId = uploadedFiles.get(0).getId();
        }

        User user = User.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .email(dto.email())
                .phoneNumber(dto.phoneNumber())
                .cpf(dto.cpf())
                .profileFileId(profileFileId)
                .build();

        User savedUser = userRepository.save(user);
        log.info("[USER_CREATED] userId={} email={}", savedUser.getId(), savedUser.getEmail());

        return new UserResponseDto(
                savedUser.getId(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail(),
                savedUser.getPhoneNumber(),
                savedUser.getCpf(),
                savedUser.getProfileFileId()
        );
    }
}
