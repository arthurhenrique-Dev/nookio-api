package com.henrique.nookio_api.modules.files.annotations.facade;

import com.henrique.nookio_api.modules.files.annotations.chain.FileProcess;
import com.henrique.nookio_api.modules.files.annotations.chain.ImageProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class ValidateFilesFacade {

    private static final String JPEG = "image/jpeg";
    private static final String PNG = "image/png";

    public boolean facade(MultipartFile file, String[] allowedTypes) {
        if (file == null || file.isEmpty()) return true;

        FileProcess begin = new FileProcess(allowedTypes);
        String contentType = file.getContentType();
        if (contentType != null && (contentType.equalsIgnoreCase(JPEG) || contentType.equalsIgnoreCase(PNG))) {
            begin.setNext(new ImageProcess());
        }
        return begin.handle(file);
    }
}
