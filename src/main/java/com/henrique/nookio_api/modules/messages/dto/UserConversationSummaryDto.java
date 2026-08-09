package com.henrique.nookio_api.modules.messages.dto;

import org.springframework.data.domain.Slice;

import java.time.Instant;

public record UserConversationSummaryDto(
        Integer userId,
        String userName,
        Integer partnerId,
        String partnerName,
        String partnerProfilePhotoUrl,
        String lastMessageContent,
        Instant lastMessageSendedAt,
        Integer unreadCount,
        Slice<MessageResponseDto> recentMessages
) {
}
