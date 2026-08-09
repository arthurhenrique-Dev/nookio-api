package com.henrique.nookio_api.modules.messages.dto;

import com.henrique.nookio_api.modules.files.models.File;

import java.time.Instant;

public record MessageResponseDto(
        Long id,
        Integer senderId,
        Integer receiverId,
        String content,
        File file,
        Instant sendedAt,
        Boolean visualized
) {
}
