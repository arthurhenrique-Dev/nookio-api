package com.henrique.nookio_publisher.listener;

import com.henrique.nookio_publisher.config.KafkaConfig;
import com.henrique.nookio_publisher.services.SesEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailMessageListener {

    private final SesEmailService sesEmailService;

    @KafkaListener(topics = KafkaConfig.EMAIL_SEND_TOPIC, groupId = "nookio-publisher-group")
    public void receiveEmailMessage(Map<String, String> payload) {
        String email = payload.get("email");
        String subject = payload.get("subject");
        String content = payload.get("content");
        log.info("[KAFKA_EMAIL_RECEIVED] to={}", email);
        sesEmailService.sendEmail(email, subject, content);
    }
}
