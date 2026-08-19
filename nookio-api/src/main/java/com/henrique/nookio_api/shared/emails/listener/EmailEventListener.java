package com.henrique.nookio_api.shared.emails.listener;

import com.henrique.nookio_api.infraestructure.microsservices.publisher.PublisherPort;
import com.henrique.nookio_api.shared.emails.event.SendEmailEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailEventListener {

    private final PublisherPort publisherPort;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSendEmailEvent(SendEmailEvent event) {
        if (event == null || event.email() == null) return;
        publisherPort.send(event.email(), event.subject(), event.content());
    }
}
