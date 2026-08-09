package com.henrique.nookio_api.modules.files.services;

import com.henrique.nookio_api.core.exceptions.FileUploadException;
import com.henrique.nookio_api.infraestructure.bucket.ports.BucketPort;
import com.henrique.nookio_api.modules.files.models.File;
import com.henrique.nookio_api.modules.files.repository.FileRepository;
import com.henrique.nookio_api.shared.logging.LogContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository repository;
    private final BucketPort bucketPort;

    @Transactional
    public List<File> upload(List<MultipartFile> files) {
        String debugId = LogContext.getDebugId();
        if (files == null || files.isEmpty()) throw new FileUploadException("Nenhum arquivo enviado para upload.");

        List<File> result = new ArrayList<>(files.size());
        List<String> uploadedFiles = new ArrayList<>(files.size());
        log.info("[FILE_UPLOAD_STARTED] debugId={} filesCount={}", debugId, files.size());

        try {
            for (MultipartFile file : files) {
                File uploaded = bucketPort.upload(file)
                        .orElseThrow(() -> new FileUploadException("Falha ao enviar o arquivo " + file.getOriginalFilename() + " para o bucket."));
                uploadedFiles.add(uploaded.getUrl());

                File saved = repository.saveAndFlush(uploaded);
                result.add(saved);
                log.info("[FILE_SAVED] debugId={} fileId={} url={}", debugId, saved.getId(), saved.getUrl());
            }
            return result;
        } catch (Exception e) {
            log.error("[FILE_UPLOAD_FAILED] debugId={} cleaning up {} files due to error: {}", debugId, uploadedFiles.size(), e.getMessage());
            uploadedFiles.forEach(bucketPort::delete);
            throw new FileUploadException("Erro ao processar upload de arquivos: " + e.getMessage(), e);
        }
    }
}
