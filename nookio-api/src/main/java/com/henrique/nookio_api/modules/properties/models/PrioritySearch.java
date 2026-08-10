package com.henrique.nookio_api.modules.properties.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(schema = "properties", name = "priority")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrioritySearch {

    @Id
    @Column(name = "params", nullable = false, length = 512)
    private String params;

    @Column(name = "searchs", nullable = false)
    private Integer searchs;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
}
