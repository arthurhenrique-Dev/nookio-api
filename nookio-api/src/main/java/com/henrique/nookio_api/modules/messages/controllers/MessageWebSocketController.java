package com.henrique.nookio_api.modules.messages.controllers;

import com.henrique.nookio_api.modules.messages.dto.MessageResponseDto;
import com.henrique.nookio_api.modules.messages.dto.SendMessageDto;
import com.henrique.nookio_api.modules.messages.services.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void processMessage(@Payload @Valid SendMessageDto dto) {
        log.info("[WS_MESSAGE_RECEIVED] senderId={} receiverId={}", dto.senderId(), dto.receiverId());
        MessageResponseDto response = messageService.sendMessage(dto);

        String topicDestination = "/topic/messages." + getPairKey(dto.senderId(), dto.receiverId());
        messagingTemplate.convertAndSend(topicDestination, response);

        String userQueueDestination = "/queue/messages";
        messagingTemplate.convertAndSendToUser(dto.receiverId().toString(), userQueueDestination, response);
    }

    @MessageMapping("/chat.visualize")
    public void markAsVisualized(@Payload SendMessageDto dto) {
        if (dto.receiverId() != null && dto.senderId() != null) {
            messageService.markAsVisualized(dto.receiverId(), dto.senderId());
            log.info("[WS_MESSAGES_VISUALIZED] receiverId={} senderId={}", dto.receiverId(), dto.senderId());
        }
    }

    private String getPairKey(Integer u1, Integer u2) {
        return u1 < u2 ? u1 + "_" + u2 : u2 + "_" + u1;
    }
}
