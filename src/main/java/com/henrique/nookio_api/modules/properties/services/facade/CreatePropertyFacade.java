package com.henrique.nookio_api.modules.properties.services.facade;

import com.henrique.nookio_api.modules.properties.dto.RegisterPropertyDto;
import com.henrique.nookio_api.modules.properties.dto.SnapshotProperty;
import com.henrique.nookio_api.modules.properties.models.Property;
import com.henrique.nookio_api.modules.properties.services.chains.CreatePropertyChain;
import com.henrique.nookio_api.modules.users.models.User;
import com.henrique.nookio_api.modules.users.repositories.UserRepository;
import com.henrique.nookio_api.shared.emails.event.SendEmailEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreatePropertyFacade {

    private final List<CreatePropertyChain> chains;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Property execute(RegisterPropertyDto dto) {
        SnapshotProperty snapshot = new SnapshotProperty();
        for (CreatePropertyChain chain : chains) {
            chain.handle(dto, snapshot);
        }

        Property property = snapshot.getSavedProperty();
        if (property == null) return null;

        Integer ownerId = property.getOwnerId();
        if (ownerId == null) return property;

        var ownerOpt = userRepository.findById(ownerId);
        if (ownerOpt.isEmpty()) return property;

        User owner = ownerOpt.get();
        String ownerEmail = owner.getEmail();
        Integer propertyId = property.getId();

        String subject = "Property #%d created successfully!".formatted(propertyId);
        String content = "Hello %s, your property #%d has been registered successfully in Nookio.".formatted(owner.getFirstName(), propertyId);

        log.info("[PROPERTY_CREATED_PUBLISH_EMAIL] propertyId={} ownerId={} email={}", propertyId, ownerId, ownerEmail);
        eventPublisher.publishEvent(new SendEmailEvent(ownerEmail, subject, content));

        return property;
    }
}
