package com.henrique.nookio_api.modules.properties.services.update;

import com.henrique.nookio_api.modules.properties.dto.InformationUpdate;
import com.henrique.nookio_api.modules.properties.models.Property;
import com.henrique.nookio_api.modules.properties.models.PropertyInformation;
import com.henrique.nookio_api.modules.properties.models.PropertyInformationDetails;
import com.henrique.nookio_api.modules.properties.repository.PropertyInformationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UpdatePropertyInformationStrategy {

    private final PropertyInformationRepository repository;

    public void update(Property property, InformationUpdate update) {
        if (update == null) return;
        try {
            PropertyInformation information = repository.findById(property.getInformationId())
                    .orElseThrow(() -> new IllegalArgumentException("Informações do imóvel não encontradas."));

            PropertyInformationDetails details = getOrCreateDetails(information);
            updateDetails(details, update);

            repository.save(information);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private PropertyInformationDetails getOrCreateDetails(PropertyInformation information) {
        if (information.getPropertyInformationDetails() == null) {
            PropertyInformationDetails newDetails = new PropertyInformationDetails();
            information.setPropertyInformationDetails(newDetails);
            return newDetails;
        }
        return information.getPropertyInformationDetails();
    }

    private void updateDetails(PropertyInformationDetails details, InformationUpdate update) {
        if (update.bedrooms() != null) details.setBedrooms(update.bedrooms());
        if (update.bathrooms() != null) details.setBathrooms(update.bathrooms());
        if (update.beds() != null) details.setBeds(update.beds());
        if (update.maxGuests() != null) details.setMaxGuests(update.maxGuests());
        if (update.parkingSpaces() != null) details.setParkingSpaces(update.parkingSpaces());
        if (update.areaSqm() != null) details.setAreaSqm(update.areaSqm());
        if (update.pools() != null) details.setPools(update.pools());
        if (update.price() != null) details.setPrice(update.price());
        if (update.cleaningFee() != null) details.setCleaningFee(update.cleaningFee());

        details.setHasWifi(update.hasWifi());
        details.setHasAirConditioning(update.hasAirConditioning());
    }
}
