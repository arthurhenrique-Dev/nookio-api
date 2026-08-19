package com.henrique.nookio_api.modules.properties.validator;

public class PropertyPhotosValidator {

    private PropertyPhotosValidator() {
    }

    public static final int MAX_PHOTOS_PER_PROPERTY = 20;

    public static void validateMaxPhotosLimit(int currentCount, int newCount) {
        if (currentCount + newCount > MAX_PHOTOS_PER_PROPERTY) {
            throw new IllegalArgumentException(
                    String.format("Um imóvel não pode ter mais de %d fotos. Tentativa de registrar %d fotos no total.",
                            MAX_PHOTOS_PER_PROPERTY, currentCount + newCount)
            );
        }
    }

    public static void validateMaxPhotosLimit(int totalCount) {
        validateMaxPhotosLimit(0, totalCount);
    }
}
