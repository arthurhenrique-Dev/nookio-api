package com.henrique.nookio_api.modules.files.annotations.chain;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.DetectModerationLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.DetectModerationLabelsResponse;

import java.io.InputStream;

@Slf4j
@NoArgsConstructor
public class ImageProcess extends ValidateProcess {

    @Override
    protected boolean validate(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            RekognitionClient rekognitionClient = RekognitionClient.create();
            SdkBytes sdkBytes = SdkBytes.fromInputStream(is);
            DetectModerationLabelsRequest request = DetectModerationLabelsRequest.builder()
                    .image(img -> img.bytes(sdkBytes))
                    .minConfidence(80F)
                    .build();

            DetectModerationLabelsResponse response = rekognitionClient.detectModerationLabels(request);
            return response.moderationLabels().isEmpty();
        } catch (Exception e) {
            log.warn("Falha ao analisar imagem via AWS Rekognition: {}", e.getMessage());
            return true;
        }
    }
}
