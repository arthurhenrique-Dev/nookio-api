package com.henrique.nookio_publisher.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;

@Slf4j
@Service
public class SesEmailService {

    @Value("${aws.ses.region:us-east-1}")
    private String awsRegion;

    public void sendEmail(String toEmail, String subject, String bodyContent) {
        if (toEmail == null || toEmail.isBlank()) return;

        try (SesClient sesClient = SesClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build()) {

            SendEmailRequest request = SendEmailRequest.builder()
                    .destination(dest -> dest.toAddresses(toEmail))
                    .message(msg -> msg
                            .subject(sub -> sub.data(subject))
                            .body(b -> b.text(txt -> txt.data(bodyContent))))
                    .source("noreply@nookio.com")
                    .build();

            sesClient.sendEmail(request);
            log.info("[AWS_SES_SEND_SUCCESS] region={} to={} subject={}", awsRegion, toEmail, subject);
        } catch (Exception e) {
            log.error("[AWS_SES_SEND_ERROR] region={} to={} error={}", awsRegion, toEmail, e.getMessage());
        }
    }
}
