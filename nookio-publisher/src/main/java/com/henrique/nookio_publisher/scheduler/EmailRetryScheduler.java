package com.henrique.nookio_publisher.scheduler;

import com.henrique.nookio_publisher.models.EmailModel;
import com.henrique.nookio_publisher.repositories.EmailRepository;
import com.henrique.nookio_publisher.services.SesEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailRetryScheduler {

    private final EmailRepository emailRepository;
    private final SesEmailService sesEmailService;

    @Scheduled(fixedDelay = 300000) // Runs every 5 minutes (300,000 ms)
    public void retryPendingEmails() {
        List<EmailModel> pendingEmails = emailRepository.findByStatus("PENDING");
        if (pendingEmails.isEmpty()) return;

        log.info("[EMAIL_RETRY_SCHEDULER] Found {} pending emails to retry.", pendingEmails.size());
        for (EmailModel email : pendingEmails) {
            try {
                sesEmailService.sendEmail(email.getRecipient(), email.getSubject(), email.getContent());
                email.setStatus("SENT");
                emailRepository.save(email);
                log.info("[EMAIL_RETRY_SUCCESS] Resent email ID: {}", email.getId());
            } catch (Exception e) {
                email.setRetryCount(email.getRetryCount() + 1);
                if (email.getRetryCount() >= 5) {
                    email.setStatus("FAILED");
                }
                emailRepository.save(email);
                log.error("[EMAIL_RETRY_FAILED] Failed retry for email ID: {}", email.getId(), e);
                break; // Stop loop if SES is still down
            }
        }
    }
}
