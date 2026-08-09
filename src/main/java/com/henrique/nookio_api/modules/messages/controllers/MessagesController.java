package com.henrique.nookio_api.modules.messages.controllers;

import com.henrique.nookio_api.core.handler.ApiErrorResponse;
import com.henrique.nookio_api.modules.messages.dto.MessageResponseDto;
import com.henrique.nookio_api.modules.messages.dto.SendMessageDto;
import com.henrique.nookio_api.modules.messages.dto.UserConversationSummaryDto;
import com.henrique.nookio_api.modules.messages.services.ConversationService;
import com.henrique.nookio_api.modules.messages.services.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
@Tag(name = "Mensagens & Chat", description = "Endpoints para envio de mensagens, histórico de conversas e suporte a mídias (foto, áudio e vídeo)")
public class MessagesController {

    private final MessageService messageService;
    private final ConversationService conversationService;

    @PostMapping
    @Operation(summary = "Enviar mensagem", description = "Envia uma nova mensagem (texto e/ou arquivo anexo) entre remetente e destinatário.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Mensagem enviada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados da mensagem inválidos.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Remetente, destinatário ou arquivo não encontrado.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<MessageResponseDto> sendMessage(@Valid @RequestBody SendMessageDto dto) {
        MessageResponseDto response = messageService.sendMessage(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/conversation")
    @Operation(summary = "Consultar mensagens de uma conversa", description = "Retorna uma fatia (Slice) das mensagens tratadas (com fullnames e dados do File) entre 2 usuários, ordenadas por sendedAt DESC (top 20 por padrão).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mensagens da conversa retornadas com sucesso."),
            @ApiResponse(responseCode = "400", description = "Parâmetros de consulta inválidos.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Slice<MessageResponseDto>> getConversationMessages(
            @RequestParam Integer user1,
            @RequestParam Integer user2,
            Pageable pageable
    ) {
        Slice<MessageResponseDto> messages = conversationService.getConversationMessages(user1, user2, pageable);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/user-conversations/{userId}")
    @Operation(summary = "Listar conversas do usuário", description = "Retorna o resumo das conversas ativas do usuário, contendo o nome dos participantes, preview da mensagem recente e top 20 mensagens.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resumo das conversas retornado com sucesso.")
    })
    public ResponseEntity<List<UserConversationSummaryDto>> getUserConversations(@PathVariable Integer userId) {
        List<UserConversationSummaryDto> conversations = conversationService.getUserConversations(userId);
        return ResponseEntity.ok(conversations);
    }

    @PatchMapping("/visualize")
    @Operation(summary = "Marcar mensagens como visualizadas", description = "Atualiza o estado de visualização de mensagens recebidas de um determinado remetente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mensagens marcadas como visualizadas.")
    })
    public ResponseEntity<Void> markAsVisualized(
            @RequestParam Integer receiverId,
            @RequestParam Integer senderId
    ) {
        messageService.markAsVisualized(receiverId, senderId);
        return ResponseEntity.ok().build();
    }
}
