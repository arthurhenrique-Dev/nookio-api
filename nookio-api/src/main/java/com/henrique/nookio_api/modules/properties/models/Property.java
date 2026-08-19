package com.henrique.nookio_api.modules.properties.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "properties", schema = "properties")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    @Column(name = "owner_id", nullable = false)
    private Integer ownerId;

    @Column(name = "information_id", nullable = false)
    private Integer informationId;

    @Column(name = "location_id", nullable = false)
    private Integer locationId;

    private boolean active;
}
