package com.henrique.nookio_api.shared.input;

public record RangeInput<T>(
        T min,
        T max
) {
}
