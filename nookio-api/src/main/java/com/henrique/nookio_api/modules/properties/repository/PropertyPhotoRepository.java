package com.henrique.nookio_api.modules.properties.repository;

import com.henrique.nookio_api.modules.properties.models.PropertyPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyPhotoRepository extends JpaRepository<PropertyPhoto, Long> {
    List<PropertyPhoto> findAllByPropertyId(Long propertyId);
}


