package com.henrique.nookio_payments.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "NOOKIO-API")
public interface NookioApiFeignClient {

    @PostMapping("/payments/webhook")
    void sendPaymentStatusWebhook(@RequestBody Map<String, Object> payload);
}
