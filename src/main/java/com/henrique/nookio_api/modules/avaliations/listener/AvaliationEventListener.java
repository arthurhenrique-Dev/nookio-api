package com.henrique.nookio_api.modules.avaliations.listener;

import com.henrique.nookio_api.modules.avaliations.event.AvaliationCreatedEvent;
import com.henrique.nookio_api.modules.users.models.User;
import com.henrique.nookio_api.modules.users.repositories.UserRepository;
import com.henrique.nookio_api.shared.emails.EmailSender;
import com.henrique.nookio_api.shared.logging.LogContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class AvaliationEventListener {

    private final UserRepository userRepository;
    private final EmailSender emailSender;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAvaliationCreated(AvaliationCreatedEvent event) {
        if (event == null || event.ownerId() == null) return;

        Integer ownerId = event.ownerId();
        Integer avaliationId = event.avaliationId();
        Integer propertyId = event.propertyId();
        Integer avaliatorId = event.avaliatorId();
        BigDecimal rating = event.rating();
        String description = event.description();

        String debugId = LogContext.getDebugId();
        log.info("[AVALIATION_EVENT_RECEIVED] debugId={} ownerId={} avaliationId={}", debugId, ownerId, avaliationId);

        var ownerOpt = userRepository.findById(ownerId);
        if (ownerOpt.isEmpty()) {
            log.warn("[AVALIATION_EVENT_OWNER_NOT_FOUND] debugId={} ownerId={}", debugId, ownerId);
            return;
        }

        User owner = ownerOpt.get();
        String ownerEmail = owner.getEmail();

        String subject = "Your property #%d received a new avaliation".formatted(propertyId);
        StringBuilder content = new StringBuilder()
                .append("avaliation ID: #").append(avaliationId)
                .append("\nGuest ID: ").append(avaliatorId)
                .append("\nRating: ").append(rating);

        if (description != null && !description.isBlank()) {
            content.append("\nComment: ").append(description.strip());
        }

        log.info("[AVALIATION_SENDING_EMAIL] debugId={} to={} ownerId={}", debugId, ownerEmail, ownerId);
        emailSender.send(ownerEmail, subject, content.toString());
        log.info("[AVALIATION_EMAIL_DISPATCHED] debugId={} to={}", debugId, ownerEmail);
    }
}
