package com.henrique.nookio_api.shared.emails;

import com.henrique.nookio_api.infraestructure.microsservices.publisher.PublisherPort;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailSender {

    private final PublisherPort publisherPort;

    @Async
    public void send(String email, String subject, String content){
        publisherPort.send(email, subject, content);
    }
}
