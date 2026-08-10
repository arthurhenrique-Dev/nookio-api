package com.henrique.nookio_api.modules.properties.services.cache.canonicalizer;

import com.henrique.nookio_api.modules.properties.dto.InputCatalog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("catalogSearchCanonicalizer")
@RequiredArgsConstructor
public class CatalogSearchCanonicalizer {

    private final CatalogSearchSerializer serializer;
    private final CatalogSearchDeserializer deserializer;

    public String serialize(InputCatalog input) {
        return serializer.serialize(input);
    }

    public InputCatalog deserialize(String canonicalParams) {
        return deserializer.deserialize(canonicalParams);
    }
}
