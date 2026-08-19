package com.henrique.nookio_api.modules.messages.services;

import com.henrique.nookio_api.core.exceptions.ResourceNotFoundException;
import com.henrique.nookio_api.modules.files.models.File;
import com.henrique.nookio_api.modules.messages.dto.MessageResponseDto;
import com.henrique.nookio_api.modules.messages.dto.UserConversationSummaryDto;
import com.henrique.nookio_api.modules.messages.models.VwConversationMessages;
import com.henrique.nookio_api.modules.messages.models.VwUserConversations;
import com.henrique.nookio_api.modules.messages.repository.MessageRepository;
import com.henrique.nookio_api.modules.messages.repository.VwConversationMessagesRepository;
import com.henrique.nookio_api.modules.messages.repository.VwUserConversationsRepository;
import com.henrique.nookio_api.modules.users.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationService {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;

    private final VwUserConversationsRepository vwUserConversationsRepository;
    private final VwConversationMessagesRepository vwConversationMessagesRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<UserConversationSummaryDto> getUserConversations(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", userId);
        }

        List<VwUserConversations> conversations = vwUserConversationsRepository.findAllUserConversations(userId);
        if (conversations.isEmpty()) {
            return List.of();
        }

        Pageable top20 = PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE);

        return conversations.stream()
                .map(conv -> {
                    boolean isSender = conv.getSenderId().equals(userId);
                    Integer partnerId = isSender ? conv.getReceiverId() : conv.getSenderId();
                    String partnerName = isSender ? conv.getReceiverFullname() : conv.getSenderFullname();
                    String partnerProfilePhotoUrl = isSender ? conv.getReceiverProfilePhotoUrl() : conv.getSenderProfilePhotoUrl();
                    String currentUserName = isSender ? conv.getSenderFullname() : conv.getReceiverFullname();

                    Slice<VwConversationMessages> messagesSlice = vwConversationMessagesRepository
                            .findConversationMessages(userId, partnerId, top20);

                    Slice<MessageResponseDto> dtoSlice = messagesSlice.map(this::mapViewToMessageResponseDto);
                    Integer unreadCount = messageRepository.countUnreadMessages(userId, partnerId);

                    return new UserConversationSummaryDto(
                            userId,
                            currentUserName,
                            partnerId,
                            partnerName,
                            partnerProfilePhotoUrl,
                            conv.getLastMessageContent(),
                            conv.getLastMessageSendedAt(),
                            unreadCount,
                            dtoSlice
                    );
                })
                .toList();
    }

    public Slice<MessageResponseDto> getConversationMessages(Integer user1, Integer user2, Pageable pageable) {
        if (pageable == null) pageable = PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE);
        Slice<VwConversationMessages> messagesSlice = vwConversationMessagesRepository
                .findConversationMessages(user1, user2, pageable);

        return messagesSlice.map(this::mapViewToMessageResponseDto);
    }

    private MessageResponseDto mapViewToMessageResponseDto(VwConversationMessages view) {
        File file = null;
        if (view.getFileId() != null) {
            file = File.builder()
                    .id(view.getFileId())
                    .url(view.getFileUrl())
                    .typeFile(view.getFileType())
                    .build();
        }

        return new MessageResponseDto(
                view.getMessageId(),
                view.getSenderId(),
                view.getReceiverId(),
                view.getContent(),
                file,
                view.getSendedAt(),
                view.getVisualized()
        );
    }
}