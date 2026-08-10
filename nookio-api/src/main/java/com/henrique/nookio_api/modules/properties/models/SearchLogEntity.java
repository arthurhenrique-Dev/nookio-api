package com.henrique.nookio_api.modules.properties.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(schema = "properties", name = "search_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "canonical_params", nullable = false, length = 512)
    private String canonicalParams;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
}
