package com.henrique.nookio_api.infraestructure.microsservices.payment;

import com.henrique.nookio_api.infraestructure.microsservices.payment.dto.PaymentResponseDto;
import com.henrique.nookio_api.modules.schedules.dto.PaymentRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "NOOKIO-PAYMENTS-API")
public interface PaymentsFeignClient {

    @PostMapping("/payments")
    PaymentResponseDto sendPayment(@RequestBody PaymentRequestDto request);

    @DeleteMapping("/payments")
    void repay(@RequestBody List<UUID> payments);
}
