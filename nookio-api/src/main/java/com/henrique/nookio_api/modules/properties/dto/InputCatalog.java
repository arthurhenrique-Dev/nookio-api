package com.henrique.nookio_api.modules.properties.dto;

import com.henrique.nookio_api.shared.input.InputPreSet;

public record InputCatalog(
        InputPreSet inputPreSet,
        String search,
        CatalogInfoInput info
) {
}
