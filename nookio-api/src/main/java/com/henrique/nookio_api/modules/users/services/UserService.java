package com.henrique.nookio_api.modules.users.services;

import com.henrique.nookio_api.core.exceptions.ResourceNotFoundException;
import com.henrique.nookio_api.modules.files.models.File;
import com.henrique.nookio_api.modules.files.services.FileService;
import com.henrique.nookio_api.modules.users.dto.CreateUserDto;
import com.henrique.nookio_api.modules.users.dto.UserEmailUpdateWebhookDto;
import com.henrique.nookio_api.modules.users.dto.UserResponseDto;
import com.henrique.nookio_api.modules.users.models.User;
import com.henrique.nookio_api.modules.users.repositories.UserRepository;
import com.henrique.nookio_api.shared.emails.event.SendEmailEvent;
import com.henrique.nookio_api.shared.logging.LogContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FileService fileService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public UserResponseDto createUser(CreateUserDto dto) {
        String email = dto.email();
        log.info("[CREATE_USER_REQUEST] email={}", email);

        Integer profileFileId = null;
        if (dto.profileFile() != null && !dto.profileFile().isEmpty()) {
            List<File> uploadedFiles = fileService.upload(List.of(dto.profileFile()));
            if (uploadedFiles != null && !uploadedFiles.isEmpty()) profileFileId = uploadedFiles.get(0).getId();
        }

        String firstName = dto.firstName();
        String lastName = dto.lastName();

        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phoneNumber(dto.phoneNumber())
                .cpf(dto.cpf())
                .profileFileId(profileFileId)
                .build();

        User savedUser = userRepository.save(user);
        Integer userId = savedUser.getId();
        log.info("[USER_CREATED] userId={} email={}", userId, email);

        String subject = "WELCOME TO NOOKIO";
        String content = "Hello %s, thanks for registering in Nookio! We are so happy having you here.".formatted(firstName);
        eventPublisher.publishEvent(new SendEmailEvent(email, subject, content));

        return new UserResponseDto(
                userId,
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail(),
                savedUser.getPhoneNumber(),
                savedUser.getCpf(),
                savedUser.getProfileFileId()
        );
    }

    @Transactional
    public void updateEmailViaWebhook(UserEmailUpdateWebhookDto dto) {
        String debugId = LogContext.getDebugId();
        Integer userId = dto.userId();
        String newEmail = dto.newEmail();

        log.info("[USER_EMAIL_WEBHOOK_RECEIVED] debugId={} userId={} newEmail={}", debugId, userId, newEmail);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        user.setEmail(newEmail);
        userRepository.save(user);
        log.info("[USER_EMAIL_WEBHOOK_UPDATED] debugId={} userId={} updatedEmail={}", debugId, userId, newEmail);
    }
}
