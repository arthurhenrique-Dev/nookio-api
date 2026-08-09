package com.henrique.nookio_api.modules.files.controllers;

import com.henrique.nookio_api.core.audit_logs.annotation.AuditLog;
import com.henrique.nookio_api.core.handler.ApiErrorResponse;
import com.henrique.nookio_api.modules.files.models.File;
import com.henrique.nookio_api.modules.files.services.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Tag(name = "Arquivos", description = "Endpoints para upload e gestão de arquivos/imagens no S3/Bucket")
public class FilesController {

    private final FileService fileService;

    @AuditLog(resource = "FILES", operation = "UPLOAD")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Fazer upload de arquivos", description = "Envia uma ou mais imagens/arquivos para o bucket de armazenamento.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Upload concluído com sucesso."),
            @ApiResponse(responseCode = "400", description = "Nenhum arquivo enviado ou tipo não permitido.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro no armazenamento ou no processamento do arquivo.",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<List<File>> upload(@RequestParam("files") List<MultipartFile> files) {
        List<File> result = fileService.upload(files);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
