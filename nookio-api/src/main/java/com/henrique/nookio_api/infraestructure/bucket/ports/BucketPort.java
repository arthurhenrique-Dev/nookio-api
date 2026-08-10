package com.henrique.nookio_api.infraestructure.bucket.ports;

import com.henrique.nookio_api.modules.files.models.File;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface BucketPort {

    Optional<File> upload(MultipartFile file);
    List<MultipartFile> getImages(List<String> keys);
    void delete(String key);
}
