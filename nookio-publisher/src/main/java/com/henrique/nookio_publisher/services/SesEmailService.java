package com.henrique.nookio_publisher.services;

import com.henrique.nookio_publisher.models.EmailModel;
import com.henrique.nookio_publisher.repositories.EmailRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class SesEmailService {

    private final EmailRepository emailRepository;

    @Value("${aws.ses.region:us-east-1}")
    private String awsRegion;

    @CircuitBreaker(name = "emailService", fallbackMethod = "fallbackSaveEmail")
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
            throw new RuntimeException("Failed to send email via AWS SES", e);
        }
    }

    public void fallbackSaveEmail(String toEmail, String subject, String bodyContent, Throwable t) {
        log.warn("[EMAIL_FALLBACK_TRIGGERED] Circuit Breaker OPEN or SES failure. Saving email to DB for retry. recipient={} subject={}", toEmail, subject);
        try {
            EmailModel emailModel = EmailModel.builder()
                    .recipient(toEmail)
                    .subject(subject)
                    .content(bodyContent)
                    .status("PENDING")
                    .retryCount(0)
                    .build();
            emailRepository.save(emailModel);
        } catch (Exception ex) {
            log.error("[EMAIL_FALLBACK_ERROR] Failed to save fallback email to DB", ex);
        }
    }
}
