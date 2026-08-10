package com.henrique.nookio_api.modules.properties.services.cache.canonicalizer;

import com.henrique.nookio_api.modules.location.dto.LocationInput;
import com.henrique.nookio_api.modules.properties.dto.CatalogInfoInput;
import com.henrique.nookio_api.modules.properties.dto.InputCatalog;
import com.henrique.nookio_api.modules.properties.models.PropertyType;
import com.henrique.nookio_api.shared.input.InputPreSet;
import com.henrique.nookio_api.shared.input.RangeInput;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CatalogSearchSerializer {

    private static final String PARAM_PROPERTY_TYPES = "propertyTypes";
    private static final String PARAM_NEXT_TO_BEACH = "nextToBeach";
    private static final String PARAM_PET_FRIENDLY = "petFriendly";
    private static final String PARAM_HAS_WIFI = "hasWifi";
    private static final String PARAM_HAS_AIR_CONDITIONING = "hasAirConditioning";
    private static final String PARAM_FAVORABLE_SEASON = "favorableSeason";
    private static final String PARAM_CITY = "city";
    private static final String PARAM_STATE = "state";
    private static final String PARAM_NEIGHBORHOOD = "neighborhood";
    private static final String PARAM_STREET = "street";
    private static final String PARAM_ZIP_CODE = "zipCode";
    private static final String PARAM_COUNTRY = "country";

    public String serialize(InputCatalog input) {
        if (input == null) return "all";

        Map<String, String> map = new TreeMap<>();
        serializePaginationAndSort(map, input.inputPreSet());

        if (input.search() != null && !input.search().isBlank()) map.put("search", input.search().trim().toLowerCase(Locale.ROOT));

        if (input.info() != null) serializeCatalogInfo(map, input.info());

        return map.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("|"));
    }

    private void serializePaginationAndSort(Map<String, String> map, InputPreSet preSet) {
        if (preSet == null) {
            map.put("page", "0");
            map.put("size", "20");
            return;
        }

        map.put("page", String.valueOf(preSet.pageNumber()));
        map.put("size", String.valueOf(preSet.pageSize()));

        if (preSet.sort() == null || preSet.sort().isEmpty()) return;

        List<String> sortedSort = preSet.sort().stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .sorted()
                .toList();
        if (!sortedSort.isEmpty()) map.put("sort", String.join(",", sortedSort));
    }

    private void serializeCatalogInfo(Map<String, String> map, CatalogInfoInput info) {
        if (info.propertyTypes() != null && !info.propertyTypes().isEmpty()) {
            String types = info.propertyTypes().stream()
                    .filter(Objects::nonNull)
                    .map(PropertyType::name)
                    .sorted()
                    .collect(Collectors.joining(","));
            if (!types.isBlank()) map.put(PARAM_PROPERTY_TYPES, types);
        }

        addRange(map, "bedrooms", info.bedrooms(), Object::toString);
        addRange(map, "bathrooms", info.bathrooms(), Object::toString);
        addRange(map, "beds", info.beds(), Object::toString);
        addRange(map, "maxGuests", info.maxGuests(), Object::toString);
        addRange(map, "parkingSpaces", info.parkingSpaces(), Object::toString);
        addRange(map, "areaSqm", info.areaSqm(), Object::toString);
        addRange(map, "pools", info.pools(), Object::toString);

        if (info.nextToBeach() != null) map.put(PARAM_NEXT_TO_BEACH, info.nextToBeach().toString());
        if (info.petFriendly() != null) map.put(PARAM_PET_FRIENDLY, info.petFriendly().toString());
        if (info.hasWifi() != null) map.put(PARAM_HAS_WIFI, info.hasWifi().toString());
        if (info.hasAirConditioning() != null) map.put(PARAM_HAS_AIR_CONDITIONING, info.hasAirConditioning().toString());
        if (info.favorableSeason() != null) map.put(PARAM_FAVORABLE_SEASON, info.favorableSeason().name());

        addRange(map, "price", info.price(), Object::toString);
        addRange(map, "cleaningFee", info.cleaningFee(), Object::toString);

        if (info.locationInput() != null) serializeLocation(map, info.locationInput());
    }

    private void serializeLocation(Map<String, String> map, LocationInput loc) {
        if (loc.city() != null && !loc.city().isBlank()) map.put(PARAM_CITY, loc.city().trim().toLowerCase(Locale.ROOT));
        if (loc.state() != null && !loc.state().isBlank()) map.put(PARAM_STATE, loc.state().trim().toLowerCase(Locale.ROOT));
        if (loc.neighborhood() != null && !loc.neighborhood().isBlank()) map.put(PARAM_NEIGHBORHOOD, loc.neighborhood().trim().toLowerCase(Locale.ROOT));
        if (loc.street() != null && !loc.street().isBlank()) map.put(PARAM_STREET, loc.street().trim().toLowerCase(Locale.ROOT));
        if (loc.zipCode() != null && !loc.zipCode().isBlank()) map.put(PARAM_ZIP_CODE, loc.zipCode().trim());
        if (loc.country() != null && !loc.country().isBlank()) map.put(PARAM_COUNTRY, loc.country().trim().toLowerCase(Locale.ROOT));
    }

    private <T> void addRange(Map<String, String> map, String key, RangeInput<T> range, Function<T, String> formatter) {
        if (range != null && (range.min() != null || range.max() != null)) {
            String minStr = range.min() != null ? formatter.apply(range.min()) : "";
            String maxStr = range.max() != null ? formatter.apply(range.max()) : "";
            map.put(key, minStr + ".." + maxStr);
        }
    }
}
