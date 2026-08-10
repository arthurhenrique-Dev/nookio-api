package com.henrique.nookio_api.modules.messages.services;

import com.henrique.nookio_api.core.exceptions.ResourceNotFoundException;
import com.henrique.nookio_api.modules.files.models.File;
import com.henrique.nookio_api.modules.files.repository.FileRepository;
import com.henrique.nookio_api.modules.files.services.FileService;
import com.henrique.nookio_api.modules.messages.dto.MessageResponseDto;
import com.henrique.nookio_api.modules.messages.dto.SendMessageDto;
import com.henrique.nookio_api.modules.messages.models.Message;
import com.henrique.nookio_api.modules.messages.repository.MessageRepository;
import com.henrique.nookio_api.modules.users.models.User;
import com.henrique.nookio_api.modules.users.repositories.UserRepository;
import com.henrique.nookio_api.shared.logging.LogContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    private final FileService fileService;

    @Transactional
    public MessageResponseDto sendMessage(SendMessageDto dto) {
        String debugId = LogContext.getDebugId();
        boolean hasFile = dto.file() != null && !dto.file().isEmpty();
        log.info("[SEND_MESSAGE] debugId={} senderId={} receiverId={} hasFile={}",
                debugId, dto.senderId(), dto.receiverId(), hasFile);

        User sender = userRepository.findById(dto.senderId())
                .orElseThrow(() -> new ResourceNotFoundException("Sender User", dto.senderId()));
        User receiver = userRepository.findById(dto.receiverId())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver User", dto.receiverId()));

        File attachedFile = null;
        if (hasFile) {
            List<File> uploadedFiles = fileService.upload(List.of(dto.file()));
            if (!uploadedFiles.isEmpty()) {
                attachedFile = uploadedFiles.get(0);
            }
        }

        Message message = Message.builder()
                .senderId(sender.getId())
                .receiverId(receiver.getId())
                .content(dto.content())
                .fileId(attachedFile != null ? attachedFile.getId() : null)
                .build();

        Message saved = messageRepository.save(message);
        log.info("[MESSAGE_SENT_SUCCESS] debugId={} messageId={} fileId={}",
                debugId, saved.getId(), saved.getFileId());

        return new MessageResponseDto(
                saved.getId(),
                saved.getSenderId(),
                saved.getReceiverId(),
                saved.getContent(),
                attachedFile,
                saved.getSendedAt(),
                saved.getVisualized()
        );
    }

    @Transactional
    public void markAsVisualized(Integer receiverId, Integer senderId) {
        messageRepository.markAsVisualized(receiverId, senderId);
    }
}