package com.henrique.nookio_api.modules.properties.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FallbackableUpload {

    boolean isUploaded;
    String key;

}
