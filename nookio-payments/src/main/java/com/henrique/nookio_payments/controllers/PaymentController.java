package com.henrique.nookio_payments.controllers;

import com.henrique.nookio_payments.core.audit_logs.annotation.AuditLog;
import com.henrique.nookio_payments.core.idempotency.annotation.Idempotency;
import com.henrique.nookio_payments.dto.PaymentRequestDto;
import com.henrique.nookio_payments.dto.PaymentResponseDto;
import com.henrique.nookio_payments.services.StripePaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final StripePaymentService stripePaymentService;

    @Idempotency
    @AuditLog(resource = "PAYMENT", operation = "PROCESS")
    @PostMapping
    public ResponseEntity<PaymentResponseDto> processPayment(@RequestBody PaymentRequestDto dto) {
        PaymentResponseDto response = stripePaymentService.processPayment(dto);
        return ResponseEntity.ok(response);
    }

    @Idempotency
    @AuditLog(resource = "PAYMENT", operation = "REPAY")
    @PostMapping("/repay")
    public ResponseEntity<PaymentResponseDto> repayPayment(@RequestBody PaymentRequestDto dto) {
        PaymentResponseDto response = stripePaymentService.processPayment(dto);
        return ResponseEntity.ok(response);
    }
}
