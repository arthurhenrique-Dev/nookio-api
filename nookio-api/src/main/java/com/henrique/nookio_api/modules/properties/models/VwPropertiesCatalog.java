package com.henrique.nookio_api.modules.properties.models;

import com.henrique.nookio_api.modules.files.models.File;
import com.henrique.nookio_api.modules.location.models.Location;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vw_properties_catalog", schema = "properties")
@Immutable
public class VwPropertiesCatalog {

    @Id
    @Column(name = "property_id")
    private Integer propertyId;

    @Column(name = "owner_id")
    private Integer ownerId;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(name = "owner_profile_photo_url")
    private String ownerProfilePhotoUrl;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "information_id")
    private PropertyInformation information;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "principal_photo_id")
    private File file;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", referencedColumnName = "property_id", insertable = false, updatable = false)
    @OrderBy("photoOrder ASC")
    private List<PropertyPhoto> photos;

    private BigDecimal avaliation;

    @Column(name = "total_schedules")
    private Integer totalSchedules;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "occupations", columnDefinition = "jsonb")
    private List<OccupationPeriod> occupations;
}
