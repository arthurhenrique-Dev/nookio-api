package com.henrique.nookio_api.modules.location.ports;

import com.henrique.nookio_api.modules.location.dto.LocationInput;
import com.henrique.nookio_api.modules.location.models.Location;
import com.henrique.nookio_api.modules.location.models.LocationInformation;

public interface AddressClient {

    LocationInformation clientAddress(LocationInput input);
}
