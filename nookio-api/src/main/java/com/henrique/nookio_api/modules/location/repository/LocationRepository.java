package com.henrique.nookio_api.modules.location.repository;

import com.henrique.nookio_api.modules.location.models.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Integer> {
}
