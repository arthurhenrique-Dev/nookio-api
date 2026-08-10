package com.henrique.nookio_api.modules.messages.dto;

import com.henrique.nookio_api.modules.files.annotations.annotation.ValidFile;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record SendMessageDto(
        @NotNull(message = "O ID do remetente é obrigatório")
        Integer senderId,

        @NotNull(message = "O ID do destinatário é obrigatório")
        Integer receiverId,

        String content,

        @ValidFile(allowedTypes = {"image/jpeg", "image/png", "image/webp", "image/gif"}, message = "Apenas imagens (fotos) são permitidas no momento")
        MultipartFile file
) {
}
