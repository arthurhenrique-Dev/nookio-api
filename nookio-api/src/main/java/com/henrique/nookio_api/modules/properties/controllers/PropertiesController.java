package com.henrique.nookio_api.modules.properties.controllers;

import com.henrique.nookio_api.core.audit_logs.annotation.AuditLog;
import com.henrique.nookio_api.core.handler.ApiErrorResponse;
import com.henrique.nookio_api.modules.avaliations.models.Avaliation;
import com.henrique.nookio_api.modules.avaliations.services.AvaliationsService;
import com.henrique.nookio_api.modules.properties.dto.RegisterPropertyDto;
import com.henrique.nookio_api.modules.properties.dto.UpdatePropertyDto;
import com.henrique.nookio_api.modules.properties.services.PropertiesService;
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

@RestController
@RequestMapping("/properties")
@RequiredArgsConstructor
@Tag(name = "Imóveis", description = "Endpoints para cadastro, edição, exclusão de imóveis e consulta de avaliações")
public class PropertiesController {

    private final PropertiesService propertiesService;
    private final AvaliationsService avaliationsService;

    @PostMapping
    @AuditLog(resource = "PROPERTIES", operation = "REGISTER")
    @Operation(summary = "Cadastrar imóvel", description = "Cadastra um novo imóvel na plataforma.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Imóvel cadastrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados do imóvel inválidos.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Violação de regra de negócio.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> createProperty(@Valid @RequestBody RegisterPropertyDto dto) {
        propertiesService.createProperty(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping
    @AuditLog(resource = "PROPERTIES", operation = "UPDATE")
    @Operation(summary = "Atualizar imóvel", description = "Atualiza dados de um imóvel existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Imóvel atualizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados de atualização inválidos.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Imóvel não encontrado.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> updateProperty(@Valid @RequestBody UpdatePropertyDto dto) {
        propertiesService.updateProperty(dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @AuditLog(resource = "PROPERTIES", operation = "DEACTIVATE")
    @Operation(summary = "Excluir imóvel", description = "Desativa/exclui um imóvel do proprietário informado.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Imóvel excluído/desativado com sucesso."),
            @ApiResponse(responseCode = "403", description = "Apenas o proprietário pode excluir o imóvel.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Imóvel não encontrado.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteProperty(@PathVariable Integer id, @RequestParam Integer ownerId) {
        propertiesService.deleteProperty(id, ownerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/avaliations")
    @AuditLog(resource = "AVALIATIONS", operation = "VISUALIZE")
    @Operation(summary = "Consultar avaliações do imóvel", description = "Retorna uma lista paginada das avaliações recebidas por um imóvel específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Avaliações retornadas com sucesso."),
            @ApiResponse(responseCode = "404", description = "Imóvel não encontrado.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Slice<Avaliation>> getPropertyAvaliations(@PathVariable Integer id, Pageable pageable) {
        Slice<Avaliation> avaliations = avaliationsService.findByPropertyId(id, pageable);
        return ResponseEntity.ok(avaliations);
    }
}
