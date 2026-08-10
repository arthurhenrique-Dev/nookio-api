package com.henrique.nookio_api.modules.location.dto;

public record LocationInput(
        String city,
        String state,
        String neighborhood,
        String street,
        String zipCode,
        String country
) {
}
