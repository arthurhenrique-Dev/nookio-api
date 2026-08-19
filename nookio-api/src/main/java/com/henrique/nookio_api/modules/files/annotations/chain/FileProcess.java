package com.henrique.nookio_api.modules.files.annotations.chain;

import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Arrays;

@RequiredArgsConstructor
public class FileProcess extends ValidateProcess {

    private static final Tika tika = new Tika();
    private final String[] allowedTypes;
    public static final long MAX_FILE_SIZE = 30L * 1024 * 1024; // 30MB

    @Override
    protected boolean validate(MultipartFile file) {
        if (file == null || file.isEmpty()) return false;
        if (file.getSize() > MAX_FILE_SIZE) return false;

        try (InputStream inputStream = file.getInputStream()) {
            String detectedType = tika.detect(inputStream);
            if (allowedTypes == null || allowedTypes.length == 0) return true;
            return Arrays.stream(allowedTypes).anyMatch(allowed -> allowed.equalsIgnoreCase(detectedType));
        } catch (Exception e) {
            return false;
        }
    }
}
