package com.henrique.nookio_api.modules.properties.controllers;

import com.henrique.nookio_api.core.audit_logs.annotation.AuditLog;
import com.henrique.nookio_api.core.handler.ApiErrorResponse;
import com.henrique.nookio_api.modules.properties.dto.InputCatalog;
import com.henrique.nookio_api.modules.properties.models.VwPropertiesCatalog;
import com.henrique.nookio_api.modules.properties.services.PropertiesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/catalog")
@RequiredArgsConstructor
@Tag(name = "Catálogo", description = "Endpoints para consulta filtrada e paginada do catálogo de imóveis")
public class CatalogController {

    private final PropertiesService propertiesService;

    @GetMapping
    @AuditLog(resource = "CATALOG", operation = "VISUALIZE")
    @Operation(summary = "Consultar catálogo de imóveis", description = "Retorna uma fatia (Slice) paginada de imóveis filtrados conforme parâmetros de busca.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Catálogo retornado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Parâmetro de busca ou filtro inválido.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Slice<VwPropertiesCatalog>> getCatalog(@ModelAttribute InputCatalog input) {
        Slice<VwPropertiesCatalog> result = propertiesService.getCatalog(input);
        return ResponseEntity.ok(result);
    }
}
