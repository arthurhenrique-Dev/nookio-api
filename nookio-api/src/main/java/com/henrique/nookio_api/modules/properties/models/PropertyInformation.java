package com.henrique.nookio_api.modules.properties.models;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
@Table(name = "property_informations", schema = "properties")
public class PropertyInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "property_id")
    private Integer property;

    @Embedded
    private PropertyInformationDetails propertyInformationDetails;
}
