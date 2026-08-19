package com.henrique.nookio_api.modules.properties.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PropertyInformationDetails {

    @Column(name = "property_type")
    @Enumerated(EnumType.STRING)
    private PropertyType propertyType;
    private Integer bedrooms;
    private Integer bathrooms;
    private Integer beds;
    @Column(name = "max_guests")
    private Integer maxGuests;
    @Column(name = "parking_spaces")
    private Integer parkingSpaces;
    @Column(name = "area_sqm")
    private BigDecimal areaSqm;
    private Integer pools;
    @Column(name = "next_to_beach")
    private boolean nextToBeach;
    @Column(name = "pet_friendly")
    private boolean petFriendly;
    @Column(name = "has_wifi")
    private boolean hasWifi;
    @Column(name = "has_air_conditioning")
    private boolean hasAirConditioning;
    @Column(name = "favorable_season")
    @Enumerated(EnumType.STRING)
    private Season favorableSeason;
    private BigDecimal price;
    @Column(name = "cleaning_fee")
    private BigDecimal cleaningFee;
}
