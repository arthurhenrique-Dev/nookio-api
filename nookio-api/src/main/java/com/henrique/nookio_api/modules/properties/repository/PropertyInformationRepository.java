package com.henrique.nookio_api.modules.properties.repository;

import com.henrique.nookio_api.modules.properties.models.PropertyInformation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyInformationRepository extends JpaRepository<PropertyInformation, Integer> {
}
