package com.henrique.nookio_api.modules.schedules.repository;

import com.henrique.nookio_api.modules.schedules.models.Schedule;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    Optional<Schedule> findByPaymentId(UUID paymentId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT COUNT(s) > 0 FROM Schedule s
        WHERE s.propertyId = :propertyId
          AND s.start < :end AND s.end > :start
          AND (
              s.status = 'CONFIRMED'
              OR (s.status = 'PENDING' AND s.reservatedAt > :cutoffTime)
          )
    """)
    boolean existsConflictingReservation(
            @Param("propertyId") Integer propertyId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("cutoffTime") LocalDateTime cutoffTime
    );
}
