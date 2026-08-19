package com.henrique.nookio_api.modules.properties.services.chains;

import com.henrique.nookio_api.modules.files.models.File;
import com.henrique.nookio_api.modules.location.models.Location;
import com.henrique.nookio_api.modules.location.repository.LocationRepository;
import com.henrique.nookio_api.modules.properties.dto.RegisterPropertyDto;
import com.henrique.nookio_api.modules.properties.dto.SnapshotProperty;
import com.henrique.nookio_api.modules.properties.interfaces.SnapshotMapper;
import com.henrique.nookio_api.modules.properties.models.Property;
import com.henrique.nookio_api.modules.properties.models.PropertyInformation;
import com.henrique.nookio_api.modules.properties.models.PropertyPhoto;
import com.henrique.nookio_api.modules.properties.repository.PropertiesRepository;
import com.henrique.nookio_api.modules.properties.repository.PropertyInformationRepository;
import com.henrique.nookio_api.modules.properties.repository.PropertyPhotoRepository;
import com.henrique.nookio_api.modules.properties.validator.PropertyPhotosValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;




@Component
@Order(4)
@RequiredArgsConstructor
public class SetupNewPropertyDataStep extends CreatePropertyChain {

    private final LocationRepository locationRepository;
    private final PropertyInformationRepository propertyInformationRepository;
    private final PropertiesRepository propertiesRepository;
    private final PropertyPhotoRepository propertyPhotoRepository;
    private final SnapshotMapper snapshotMapper;

    @Override
    public void step(RegisterPropertyDto dto, SnapshotProperty snapshot) {
        Location location = locationRepository.save(snapshotMapper.toLocation(snapshot));
        PropertyInformation information = propertyInformationRepository.save(snapshotMapper.toInformation(snapshot));

        Property property = snapshotMapper.toProperty(snapshot, information.getId(), location.getId());
        property = propertiesRepository.save(property);

        if (snapshot.getUploadedFiles() != null && !snapshot.getUploadedFiles().isEmpty()) {
            PropertyPhotosValidator.validateMaxPhotosLimit(snapshot.getUploadedFiles().size());

            for (int i = 0; i < snapshot.getUploadedFiles().size(); i++) {
                File file = snapshot.getUploadedFiles().get(i);
                PropertyPhoto photo = PropertyPhoto.builder()
                        .propertyId(property.getId().longValue())
                        .file(file)
                        .photoOrder(i + 1)
                        .build();
                propertyPhotoRepository.save(photo);
            }
        }


        information.setProperty(property.getId());
        propertyInformationRepository.save(information);

        snapshot.setSavedProperty(property);
    }
}
