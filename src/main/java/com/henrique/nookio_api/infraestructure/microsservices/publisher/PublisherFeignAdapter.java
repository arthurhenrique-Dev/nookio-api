package com.henrique.nookio_api.infraestructure.microsservices.publisher;

import com.henrique.nookio_api.shared.logging.LogContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PublisherFeignAdapter implements PublisherPort {

    private final PublisherFeignClient publisherFeignClient;

    @Override
    public void send(String email, String subject, String content) {
        String debugId = LogContext.getDebugId();
        log.info("[PUBLISHER_SEND_EMAIL_STARTED] debugId={} to={}", debugId, email);

        try {
            publisherFeignClient.send(email, subject, content);
            log.info("[PUBLISHER_SEND_EMAIL_SUCCESS] debugId={} to={}", debugId, email);
        } catch (Exception e) {
            log.error("[PUBLISHER_SEND_EMAIL_FAILED] debugId={} to={} error={}", debugId, email, e.getMessage());
        }
    }
}
