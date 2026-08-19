package com.henrique.nookio_api.modules.properties.dto;

import com.henrique.nookio_api.modules.files.models.File;
import com.henrique.nookio_api.modules.location.models.LocationInformation;
import com.henrique.nookio_api.modules.properties.models.Property;
import com.henrique.nookio_api.modules.properties.models.PropertyInformationDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SnapshotProperty {

    Integer ownerId;
    String title;
    PropertyInformationDetails details;
    LocationInformation location;
    List<MultipartFile> photos;
    List<File> uploadedFiles;
    Property savedProperty;
}
