package com.henrique.nookio_api.modules.properties.services.chains;

import com.henrique.nookio_api.modules.properties.dto.RegisterPropertyDto;
import com.henrique.nookio_api.modules.properties.dto.SnapshotProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class InformationPropertyStep extends CreatePropertyChain{

    @Override
    public void step(RegisterPropertyDto dto, SnapshotProperty snapshot) {
        snapshot.setDetails(dto.propertyInformationDetails());
    }
}
