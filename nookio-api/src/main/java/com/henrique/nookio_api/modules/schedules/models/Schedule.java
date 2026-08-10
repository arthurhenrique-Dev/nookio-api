package com.henrique.nookio_api.modules.schedules.models;

import com.henrique.nookio_api.modules.avaliations.models.Avaliation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "schedules", schema = "properties")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "property_id", nullable = false)
    private Integer propertyId;

    @Column(name = "guest_id", nullable = false)
    private Integer guestId;

    @Column(name = "owner_id", nullable = false)
    private Integer ownerId;

    @Column(name = "reservated_at")
    private LocalDateTime reservatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ScheduleStatus status = ScheduleStatus.PENDING;

    @Column(name = "start_date", nullable = false)
    private LocalDate start;

    @Column(name = "end_date", nullable = false)
    private LocalDate end;

    @Column(name = "check_in")
    private LocalDateTime checkIn;

    @Column(name = "check_out")
    private LocalDateTime checkOut;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_avaliation")
    private Avaliation avaliation;

    @Column(name = "payment_id")
    private UUID paymentId;
}

