package com.henrique.nookio_api.modules.properties.repository;

import com.henrique.nookio_api.modules.properties.models.VwPropertiesCatalog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;

public interface VwPropertiesCatalogRepository
        extends JpaRepository<VwPropertiesCatalog, Integer>, JpaSpecificationExecutor<VwPropertiesCatalog> {

    @EntityGraph(attributePaths = {"information", "file", "location", "photos", "photos.file"})
    Slice<VwPropertiesCatalog> findAllBy(@Nullable Specification<VwPropertiesCatalog> spec, Pageable pageable);
}
