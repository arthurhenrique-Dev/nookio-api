package com.henrique.nookio_api.modules.properties.events;

import com.henrique.nookio_api.modules.properties.dto.InputCatalog;

import java.time.LocalDateTime;

public record CatalogSearchEvent(
        String canonicalParams,
        InputCatalog inputCatalog,
        LocalDateTime timestamp
) {
    public CatalogSearchEvent(String canonicalParams, InputCatalog inputCatalog) {
        this(canonicalParams, inputCatalog, LocalDateTime.now());
    }
}
