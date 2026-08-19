package com.henrique.nookio_api.modules.avaliations.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateAvaliationDto(
        @NotNull(message = "Nota é obrigatória")
        @Min(value = 1, message = "Nota mínima é 1")
        @Max(value = 5, message = "Nota máxima é 5")
        @Digits(integer = 1, fraction = 1, message = "Nota deve ter no máximo 1 casa decimal (ex: 4.5)")
        BigDecimal rating,

        @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
        String description
) {
}
