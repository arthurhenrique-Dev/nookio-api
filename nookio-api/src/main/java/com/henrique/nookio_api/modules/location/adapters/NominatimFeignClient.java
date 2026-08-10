package com.henrique.nookio_api.modules.location.adapters;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "nominatimClient", url = "https://nominatim.openstreetmap.org")
public interface NominatimFeignClient {

    @GetMapping("/search")
    List<OpenStreetMapAddressAdapter.NominatimResponseDto> search(
            @RequestParam("q") String query,
            @RequestParam("format") String format,
            @RequestParam("addressdetails") String addressdetails,
            @RequestParam("limit") String limit,
            @RequestHeader(HttpHeaders.USER_AGENT) String userAgent,
            @RequestHeader(HttpHeaders.ACCEPT) String accept
    );
}
