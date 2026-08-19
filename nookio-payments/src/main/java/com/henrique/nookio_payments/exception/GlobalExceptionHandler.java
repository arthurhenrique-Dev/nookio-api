package com.henrique.nookio_payments.exception;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<Map<String, Object>> handleCallNotPermitted(CallNotPermittedException ex, HttpServletRequest request) {
        log.warn("[PAYMENTS_CIRCUIT_OPEN_503] path={} circuit={}", request.getRequestURI(), ex.getCausingCircuitBreakerName());
        Map<String, Object> response = new HashMap<>();
        response.put("status", 503);
        response.put("error", "SERVICE_UNAVAILABLE");
        response.put("message", "O serviço de pagamentos está em circuito aberto. Tente novamente mais tarde.");
        response.put("path", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @ExceptionHandler({TimeoutException.class, SocketTimeoutException.class, FeignException.GatewayTimeout.class})
    public ResponseEntity<Map<String, Object>> handleTimeoutException(Exception ex, HttpServletRequest request) {
        log.warn("[PAYMENTS_TIMEOUT_504] path={} error={}", request.getRequestURI(), ex.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("status", 504);
        response.put("error", "GATEWAY_TIMEOUT");
        response.put("message", "O tempo limite de processamento foi excedido.");
        response.put("path", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(response);
    }

    @ExceptionHandler({ConnectException.class, FeignException.BadGateway.class, FeignException.FeignServerException.class})
    public ResponseEntity<Map<String, Object>> handleBadGatewayException(Exception ex, HttpServletRequest request) {
        log.warn("[PAYMENTS_BAD_GATEWAY_502] path={} error={}", request.getRequestURI(), ex.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("status", 502);
        response.put("error", "BAD_GATEWAY");
        response.put("message", "Falha de comunicação com o serviço remoto.");
        response.put("path", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("[PAYMENTS_GENERIC_ERROR] path={}", request.getRequestURI(), ex);
        Map<String, Object> response = new HashMap<>();
        response.put("status", 500);
        response.put("error", "INTERNAL_SERVER_ERROR");
        response.put("message", "Ocorreu um erro interno no serviço de pagamentos.");
        response.put("path", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
