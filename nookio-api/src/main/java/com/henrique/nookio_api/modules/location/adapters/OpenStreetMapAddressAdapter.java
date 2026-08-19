package com.henrique.nookio_api.modules.location.adapters;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.henrique.nookio_api.modules.location.dto.LocationInput;
import com.henrique.nookio_api.modules.location.models.LocationInformation;
import com.henrique.nookio_api.modules.location.ports.AddressClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenStreetMapAddressAdapter implements AddressClient {

    private final NominatimFeignClient nominatimFeignClient;

    @Override
    public LocationInformation clientAddress(LocationInput input) {
        if (input == null) return LocationInformation.builder().build();

        try {
            String query = buildQueryString(input);
            log.info("Buscando localização no OpenStreetMap Nominatim via Feign para a query: {}", query);

            List<NominatimResponseDto> results = nominatimFeignClient.search(
                    query,
                    "json",
                    "1",
                    "1",
                    "NookioApi/1.0 (contact@nookio.com)",
                    "application/json"
            );

            if (results != null && !results.isEmpty()) return mapToLocationInformation(results.get(0), input);
            log.warn("Nenhum resultado encontrado no OpenStreetMap para o endereço fornecido. Usando dados informados no input.");
        } catch (Exception e) {
            log.error("Erro ao consultar OpenStreetMap Nominatim via Feign: {}", e.getMessage(), e);
        }

        return fallbackLocationInformation(input);
    }

    private String buildQueryString(LocationInput input) {
        List<String> parts = new ArrayList<>();
        if (input.street() != null && !input.street().isBlank()) parts.add(input.street());
        if (input.neighborhood() != null && !input.neighborhood().isBlank()) parts.add(input.neighborhood());
        if (input.city() != null && !input.city().isBlank()) parts.add(input.city());
        if (input.state() != null && !input.state().isBlank()) parts.add(input.state());
        if (input.zipCode() != null && !input.zipCode().isBlank()) parts.add(input.zipCode());
        if (input.country() != null && !input.country().isBlank()) parts.add(input.country());
        return String.join(", ", parts);
    }

    private LocationInformation mapToLocationInformation(NominatimResponseDto dto, LocationInput input) {
        BigDecimal lat = dto.lat() != null ? new BigDecimal(dto.lat()) : null;
        BigDecimal lon = dto.lon() != null ? new BigDecimal(dto.lon()) : null;

        NominatimAddressDto addr = dto.address();
        String street = addr != null && addr.road() != null ? addr.road() : input.street();
        String neighborhood = addr != null ? getFirstNonNull(addr.suburb(), addr.neighbourhood(), input.neighborhood()) : input.neighborhood();
        String city = addr != null ? getFirstNonNull(addr.city(), addr.town(), addr.village(), input.city()) : input.city();
        String state = addr != null && addr.state() != null ? addr.state() : input.state();
        String zipCode = addr != null && addr.postcode() != null ? addr.postcode() : input.zipCode();
        String country = addr != null && addr.country() != null ? addr.country() : input.country();

        return LocationInformation.builder()
                .street(street)
                .neighborhood(neighborhood)
                .city(city)
                .state(state)
                .zipCode(zipCode)
                .country(country)
                .latitude(lat)
                .longitude(lon)
                .build();
    }

    private LocationInformation fallbackLocationInformation(LocationInput input) {
        return LocationInformation.builder()
                .street(input.street())
                .neighborhood(input.neighborhood())
                .city(input.city())
                .state(input.state())
                .zipCode(input.zipCode())
                .country(input.country())
                .build();
    }

    private String getFirstNonNull(String... candidates) {
        for (String candidate : candidates) {
            if (candidate != null && !candidate.isBlank()) return candidate;
        }
        return null;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record NominatimResponseDto(
            String lat,
            String lon,
            NominatimAddressDto address
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record NominatimAddressDto(
            String road,
            String suburb,
            String neighbourhood,
            String city,
            String town,
            String village,
            String state,
            String postcode,
            String country
    ) {}
}
