package com.henrique.nookio_api.modules.avaliations.repository;

import com.henrique.nookio_api.modules.avaliations.models.Avaliation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvaliationRepository extends JpaRepository<Avaliation, Long> {
    Slice<Avaliation> findAllByPropertyIdOrderByCreatedAtDesc(Long propertyId, Pageable pageable);
}
