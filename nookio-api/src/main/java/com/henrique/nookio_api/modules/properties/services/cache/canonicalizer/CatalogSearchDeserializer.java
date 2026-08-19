package com.henrique.nookio_api.modules.properties.services.cache.canonicalizer;

import com.henrique.nookio_api.modules.location.dto.LocationInput;
import com.henrique.nookio_api.modules.properties.dto.CatalogInfoInput;
import com.henrique.nookio_api.modules.properties.dto.InputCatalog;
import com.henrique.nookio_api.modules.properties.models.PropertyType;
import com.henrique.nookio_api.modules.properties.models.Season;
import com.henrique.nookio_api.shared.input.InputPreSet;
import com.henrique.nookio_api.shared.input.RangeInput;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

@Component
public class CatalogSearchDeserializer {

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

    public InputCatalog deserialize(String canonicalParams) {
        if (canonicalParams == null || canonicalParams.isBlank() || "all".equalsIgnoreCase(canonicalParams))
            return new InputCatalog(null, null, null);

        Map<String, String> map = new HashMap<>();
        String[] pairs = canonicalParams.split("\\|");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) map.put(kv[0].trim(), kv[1].trim());
        }

        Integer page = map.containsKey("page") ? Integer.parseInt(map.get("page")) : 0;
        Integer size = map.containsKey("size") ? Integer.parseInt(map.get("size")) : 20;
        List<String> sort = map.containsKey("sort") ? Arrays.asList(map.get("sort").split(",")) : null;
        InputPreSet preSet = new InputPreSet(page, size, sort);

        String search = map.get("search");
        CatalogInfoInput info = deserializeCatalogInfo(map);

        return new InputCatalog(preSet, search, info);
    }

    private CatalogInfoInput deserializeCatalogInfo(Map<String, String> map) {
        List<PropertyType> propertyTypes = null;
        if (map.containsKey(PARAM_PROPERTY_TYPES)) {
            propertyTypes = Arrays.stream(map.get(PARAM_PROPERTY_TYPES).split(","))
                    .map(String::trim)
                    .map(PropertyType::valueOf)
                    .toList();
        }

        RangeInput<Integer> bedrooms = parseRange(map.get("bedrooms"), Integer::parseInt);
        RangeInput<Integer> bathrooms = parseRange(map.get("bathrooms"), Integer::parseInt);
        RangeInput<Integer> beds = parseRange(map.get("beds"), Integer::parseInt);
        RangeInput<Integer> maxGuests = parseRange(map.get("maxGuests"), Integer::parseInt);
        RangeInput<Integer> parkingSpaces = parseRange(map.get("parkingSpaces"), Integer::parseInt);
        RangeInput<BigDecimal> areaSqm = parseRange(map.get("areaSqm"), BigDecimal::new);
        RangeInput<Integer> pools = parseRange(map.get("pools"), Integer::parseInt);

        Boolean nextToBeach = parseBoolean(map.get(PARAM_NEXT_TO_BEACH));
        Boolean petFriendly = parseBoolean(map.get(PARAM_PET_FRIENDLY));
        Boolean hasWifi = parseBoolean(map.get(PARAM_HAS_WIFI));
        Boolean hasAirConditioning = parseBoolean(map.get(PARAM_HAS_AIR_CONDITIONING));
        Season favorableSeason = map.containsKey(PARAM_FAVORABLE_SEASON) ? Season.valueOf(map.get(PARAM_FAVORABLE_SEASON)) : null;

        RangeInput<BigDecimal> price = parseRange(map.get("price"), BigDecimal::new);
        RangeInput<BigDecimal> cleaningFee = parseRange(map.get("cleaningFee"), BigDecimal::new);

        LocationInput locationInput = deserializeLocation(map);

        return new CatalogInfoInput(
                propertyTypes,
                bedrooms,
                bathrooms,
                beds,
                maxGuests,
                parkingSpaces,
                areaSqm,
                pools,
                nextToBeach,
                petFriendly,
                hasWifi,
                hasAirConditioning,
                favorableSeason,
                price,
                cleaningFee,
                locationInput
        );
    }

    private LocationInput deserializeLocation(Map<String, String> map) {
        if (map.containsKey(PARAM_CITY) || map.containsKey(PARAM_STATE) || map.containsKey(PARAM_NEIGHBORHOOD) ||
            map.containsKey(PARAM_STREET) || map.containsKey(PARAM_ZIP_CODE) || map.containsKey(PARAM_COUNTRY)) {
            return new LocationInput(
                    map.get(PARAM_CITY),
                    map.get(PARAM_STATE),
                    map.get(PARAM_NEIGHBORHOOD),
                    map.get(PARAM_STREET),
                    map.get(PARAM_ZIP_CODE),
                    map.get(PARAM_COUNTRY)
            );
        }
        return null;
    }

    private <T> RangeInput<T> parseRange(String val, Function<String, T> parser) {
        if (val == null || !val.contains("..")) return null;
        String[] parts = val.split("\\.\\.", -1);
        T min = !parts[0].isBlank() ? parser.apply(parts[0]) : null;
        T max = parts.length > 1 && !parts[1].isBlank() ? parser.apply(parts[1]) : null;
        return new RangeInput<>(min, max);
    }

    private Boolean parseBoolean(String val) {
        return val != null ? Boolean.valueOf(val) : null;
    }
}
