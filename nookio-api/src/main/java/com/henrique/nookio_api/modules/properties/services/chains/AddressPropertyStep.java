package com.henrique.nookio_api.modules.properties.services.chains;
import com.henrique.nookio_api.modules.location.models.LocationInformation;
import com.henrique.nookio_api.modules.location.ports.AddressClient;
import com.henrique.nookio_api.modules.properties.dto.RegisterPropertyDto;
import com.henrique.nookio_api.modules.properties.dto.SnapshotProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Order(1)
public class AddressPropertyStep extends CreatePropertyChain{

    private final AddressClient client;

    @Override
    public void step(RegisterPropertyDto dto, SnapshotProperty snapshot) {
        LocationInformation location = client.clientAddress(dto.locationInput());
        snapshot.setLocation(location);
    }
}
