package com.henrique.nookio_api.modules.properties.services.update;

import com.henrique.nookio_api.modules.files.models.File;
import com.henrique.nookio_api.modules.files.services.FileService;
import com.henrique.nookio_api.modules.properties.dto.PhotoUpdatePayload;
import com.henrique.nookio_api.modules.properties.dto.UpdatePropertyDto;
import com.henrique.nookio_api.modules.properties.models.Property;
import com.henrique.nookio_api.modules.properties.models.PropertyPhoto;
import com.henrique.nookio_api.modules.properties.repository.PropertyPhotoRepository;
import com.henrique.nookio_api.modules.properties.validator.PropertyPhotosValidator;
import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Component
public class UpdatePropertyPhotosStrategy {

    private final PropertyPhotoRepository photoRepository;
    private final FileService fileService;

    @Transactional
    public void update(Property property, UpdatePropertyDto dto) {

        Long propertyId = property.getId().longValue();
        if (dto.swapPhotoId1() != null && dto.swapPhotoId2() != null) swapPhotosOrder(propertyId, dto.swapPhotoId1(), dto.swapPhotoId2());
        if (dto.photos() != null && !dto.photos().isEmpty()) processPhotosList(propertyId, dto.photos());
    }

    private void swapPhotosOrder(Long propertyId, Long photoId1, Long photoId2) {
        PropertyPhoto photo1 = photoRepository.findById(photoId1)
                .filter(p -> p.getPropertyId().equals(propertyId))
                .orElseThrow(() -> new IllegalArgumentException("Foto 1 não encontrada no imóvel."));
        PropertyPhoto photo2 = photoRepository.findById(photoId2)
                .filter(p -> p.getPropertyId().equals(propertyId))
                .orElseThrow(() -> new IllegalArgumentException("Foto 2 não encontrada no imóvel."));

        Integer tempOrder = photo1.getPhotoOrder();
        photo1.setPhotoOrder(photo2.getPhotoOrder());
        photo2.setPhotoOrder(tempOrder);

        photoRepository.save(photo1);
        photoRepository.save(photo2);
    }

    private void processPhotosList(Long propertyId, List<PhotoUpdatePayload> payloads) {
        List<PropertyPhoto> actualPhotos = photoRepository.findAllByPropertyId(propertyId);
        Map<Long, PropertyPhoto> actualPhotosMap = actualPhotos.stream()
                .collect(Collectors.toMap(PropertyPhoto::getId, p -> p));

        List<PhotoUpdatePayload> newPhotoPayloads = processExistingPhotosAndFilterNew(payloads, actualPhotosMap);

        PropertyPhotosValidator.validateMaxPhotosLimit(actualPhotos.size(), newPhotoPayloads.size());

        List<PropertyPhoto> newPhotos = createNewPhotos(propertyId, newPhotoPayloads, actualPhotos.size());

        List<PropertyPhoto> allPhotos = new ArrayList<>(actualPhotos);
        allPhotos.addAll(newPhotos);

        photoRepository.saveAll(allPhotos);
    }

    private List<PhotoUpdatePayload> processExistingPhotosAndFilterNew(
            List<PhotoUpdatePayload> payloads,
            Map<Long, PropertyPhoto> actualPhotosMap) {

        List<PhotoUpdatePayload> newPhotoPayloads = new ArrayList<>();

        for (PhotoUpdatePayload payload : payloads) {
            updateOrderIfExisting(payload, actualPhotosMap);

            if (isNewFile(payload)) {
                newPhotoPayloads.add(payload);
            }
        }

        return newPhotoPayloads;
    }

    private void updateOrderIfExisting(PhotoUpdatePayload payload, Map<Long, PropertyPhoto> actualPhotosMap) {
        if (payload.photoId() == null || payload.order() == null) {
            return;
        }

        PropertyPhoto existingPhoto = actualPhotosMap.get(payload.photoId());
        if (existingPhoto != null) {
            existingPhoto.setPhotoOrder(payload.order().intValue());
        }
    }

    private boolean isNewFile(PhotoUpdatePayload payload) {
        return payload.file() != null && !payload.file().isEmpty();
    }

    private List<PropertyPhoto> createNewPhotos(Long propertyId, List<PhotoUpdatePayload> newPhotoPayloads, int basePhotosCount) {
        if (newPhotoPayloads.isEmpty()) {
            return List.of();
        }

        List<MultipartFile> filesToUpload = newPhotoPayloads.stream()
                .map(PhotoUpdatePayload::file)
                .toList();

        List<File> uploadedFiles = fileService.upload(filesToUpload);
        List<PropertyPhoto> newPhotos = new ArrayList<>();

        for (int i = 0; i < newPhotoPayloads.size(); i++) {
            PhotoUpdatePayload payload = newPhotoPayloads.get(i);
            File uploadedFile = uploadedFiles.get(i);

            int targetOrder = payload.order() != null
                    ? payload.order().intValue()
                    : basePhotosCount + i + 1;

            PropertyPhoto newPhoto = PropertyPhoto.builder()
                    .propertyId(propertyId)
                    .file(uploadedFile)
                    .photoOrder(targetOrder)
                    .build();

            newPhotos.add(newPhoto);
        }

        return newPhotos;
    }
}
