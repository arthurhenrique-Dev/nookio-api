package com.henrique.nookio_api.modules.properties.repository;

import com.henrique.nookio_api.modules.properties.models.PrioritySearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PrioritySearchRepository extends JpaRepository<PrioritySearch, String> {

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO properties.priority (params, searchs, timestamp)
        VALUES (:params, 1, :timestamp)
        ON CONFLICT (params)
        DO UPDATE SET
            searchs = properties.priority.searchs + 1,
            timestamp = :timestamp
    """, nativeQuery = true)
    void incrementSearchCount(@Param("params") String params, @Param("timestamp") LocalDateTime timestamp);

    List<PrioritySearch> findTop30ByOrderBySearchsDescTimestampDesc();

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE properties.priority", nativeQuery = true)
    void truncateTable();
}
