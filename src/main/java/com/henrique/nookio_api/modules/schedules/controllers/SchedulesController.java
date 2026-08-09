package com.henrique.nookio_api.modules.schedules.controllers;

import com.henrique.nookio_api.core.audit_logs.annotation.AuditLog;
import com.henrique.nookio_api.core.handler.ApiErrorResponse;
import com.henrique.nookio_api.modules.avaliations.dto.CreateAvaliationDto;
import com.henrique.nookio_api.modules.avaliations.models.Avaliation;
import com.henrique.nookio_api.modules.avaliations.services.AvaliationsService;
import com.henrique.nookio_api.modules.schedules.dto.ReserveScheduleDto;
import com.henrique.nookio_api.modules.schedules.services.SchedulesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "Endpoints para criação de reservas, check-in, check-out, cancelamentos e avaliações")
public class SchedulesController {

    private final SchedulesService schedulesService;
    private final AvaliationsService avaliationsService;

    @PostMapping
    @AuditLog(resource = "SCHEDULES", operation = "RESERVE")
    @Operation(summary = "Realizar reserva", description = "Cria uma nova reserva de estadia para um imóvel.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reserva criada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados da reserva inválidos.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuário ou imóvel não encontrado.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Imóvel indisponível para a data selecionada.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "502", description = "Falha de comunicação com o serviço de pagamento.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> reserve(@Valid @RequestBody ReserveScheduleDto dto) {
        schedulesService.reserve(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{id}/check-in")
    @AuditLog(resource = "SCHEDULES", operation = "CHECK-IN")
    @Operation(summary = "Realizar check-in", description = "Registra a entrada do hóspede na reserva confirmada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Check-in realizado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Reserva não encontrada.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Status inválido ou check-in já realizado.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> checkIn(@PathVariable Integer id) {
        schedulesService.checkIn(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/check-out")
    @AuditLog(resource = "SCHEDULES", operation = "CHECK-OUT")
    @Operation(summary = "Realizar check-out", description = "Registra a saída do hóspede e conclui a reserva.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Check-out realizado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Reserva não encontrada.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Check-in pendente ou check-out já realizado.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> checkout(@PathVariable Integer id) {
        schedulesService.checkout(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/cancel")
    @AuditLog(resource = "SCHEDULES", operation = "CANCEL")
    @Operation(summary = "Cancelar reserva", description = "Cancela uma reserva e aciona estorno se aplicável.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva cancelada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Reserva não encontrada.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> cancel(@PathVariable Integer id) {
        schedulesService.cancel(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/avaliations")
    @AuditLog(resource = "AVALIATIONS", operation = "AVALIATE")
    @Operation(summary = "Avaliar estadia", description = "Cria uma nova avaliação para uma reserva concluída.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Avaliação registrada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados da avaliação inválidos.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Reserva não encontrada.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Reserva já possui uma avaliação registrada.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Estadia ainda não foi concluída.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Avaliation> avaliateSchedule(@PathVariable Integer id, @Valid @RequestBody CreateAvaliationDto dto) {
        Avaliation created = avaliationsService.avaliateSchedule(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
