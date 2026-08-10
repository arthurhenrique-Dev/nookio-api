package com.henrique.nookio_api.modules.properties.repository;

import com.henrique.nookio_api.modules.properties.models.SearchLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SearchLogFallbackRepository extends JpaRepository<SearchLogEntity, Long> {

    Page<SearchLogEntity> findAllByOrderByTimestampAsc(Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE properties.search_logs", nativeQuery = true)
    void truncateTable();
}
