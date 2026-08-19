package com.henrique.nookio_api.infraestructure.microsservices.publisher;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "nookio-publisher")
public interface PublisherFeignClient {

    @PostMapping("/send")
    void send(
            @RequestParam("email") String email,
            @RequestParam("subject") String subject,
            @RequestParam("content") String content
    );
}
