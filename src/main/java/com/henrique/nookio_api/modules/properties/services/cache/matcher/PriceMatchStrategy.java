package com.henrique.nookio_api.modules.properties.services.cache.matcher;

import com.henrique.nookio_api.modules.properties.dto.CatalogInfoInput;
import com.henrique.nookio_api.modules.properties.dto.InputCatalog;
import com.henrique.nookio_api.modules.properties.models.VwPropertiesCatalog;
import com.henrique.nookio_api.shared.input.RangeInput;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PriceMatchStrategy implements CatalogCacheMatchStrategy {

    @Override
    public boolean matches(InputCatalog search, VwPropertiesCatalog property) {
        if (search == null || search.info() == null || search.info().price() == null) return true;

        CatalogInfoInput info = search.info();
        BigDecimal totalPrice = property.getTotalPrice();

        if (totalPrice == null) return true;

        return matchesBigDecimalRange(info.price(), totalPrice);
    }

    private boolean matchesBigDecimalRange(RangeInput<BigDecimal> range, BigDecimal value) {
        if (range == null) return true;
        if (value == null) return false;
        return (range.min() == null || value.compareTo(range.min()) >= 0) && (range.max() == null || value.compareTo(range.max()) <= 0);
    }
}
