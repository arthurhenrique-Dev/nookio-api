package com.henrique.nookio_api.infraestructure.bucket.adapters;

import com.henrique.nookio_api.infraestructure.bucket.ports.BucketPort;
import com.henrique.nookio_api.modules.files.models.File;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class MockS3BucketAdapter implements BucketPort {

    private static final String MOCK_BUCKET_BASE_URL = "https://nookio-bucket.s3.amazonaws.com/properties/";

    @Override
    public Optional<File> upload(MultipartFile file) {
        if (file == null || file.isEmpty()) return Optional.empty();

        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "unnamed_file";
        String generatedKey = UUID.randomUUID().toString().substring(0, 8) + "_" + originalFilename;
        String mockUrl = MOCK_BUCKET_BASE_URL + generatedKey;

        log.info("[MOCK_S3_UPLOAD] Uploaded mock file to S3. originalName={} key={} url={}", originalFilename, generatedKey, mockUrl);

        File fileEntity = File.builder()
                .typeFile(file.getContentType() != null ? file.getContentType() : "image/jpeg")
                .url(mockUrl)
                .build();

        return Optional.of(fileEntity);
    }

    @Override
    public List<MultipartFile> getImages(List<String> keys) {
        log.info("[MOCK_S3_GET_IMAGES] Requested keys: {}", keys);
        return Collections.emptyList();
    }

    @Override
    public void delete(String key) {
        log.info("[MOCK_S3_DELETE] Deleted mock key from S3: {}", key);
    }
}


