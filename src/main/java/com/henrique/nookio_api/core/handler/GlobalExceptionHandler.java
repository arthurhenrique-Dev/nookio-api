package com.henrique.nookio_api.core.handler;

import com.henrique.nookio_api.core.exceptions.NookioException;
import com.henrique.nookio_api.core.exceptions.ValidationException;
import com.henrique.nookio_api.shared.logging.LogContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NookioException.class)
    public ResponseEntity<ApiErrorResponse> handleNookioException(NookioException ex, HttpServletRequest request) {
        String debugId = LogContext.getDebugId();
        log.warn("[EXCEPTION_HANDLED] debugId={} status={} errorCode={} message={}",
                debugId, ex.getStatus().value(), ex.getErrorCode(), ex.getMessage());

        List<FieldErrorDetail> details = null;
        if (ex instanceof ValidationException valEx && valEx.getField() != null)
            details = List.of(new FieldErrorDetail(valEx.getField(), valEx.getMessage()));

        ApiErrorResponse body = ApiErrorResponse.of(
                ex.getStatus().value(),
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI(),
                debugId,
                details
        );
        return ResponseEntity.status(ex.getStatus()).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String debugId = LogContext.getDebugId();
        List<FieldErrorDetail> details = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> new FieldErrorDetail(err.getField(), err.getDefaultMessage()))
                .toList();

        log.warn("[VALIDATION_FAILED] debugId={} path={} fieldErrorsCount={}", debugId, request.getRequestURI(), details.size());

        ApiErrorResponse body = ApiErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                "Um ou mais campos contêm erros de validação.",
                request.getRequestURI(),
                debugId,
                details
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        String debugId = LogContext.getDebugId();
        log.warn("[ILLEGAL_ARGUMENT] debugId={} message={}", debugId, ex.getMessage());

        ApiErrorResponse body = ApiErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "BAD_REQUEST",
                ex.getMessage() != null ? ex.getMessage() : "Parâmetro inválido.",
                request.getRequestURI(),
                debugId
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalState(IllegalStateException ex, HttpServletRequest request) {
        String debugId = LogContext.getDebugId();
        log.warn("[ILLEGAL_STATE] debugId={} message={}", debugId, ex.getMessage());

        ApiErrorResponse body = ApiErrorResponse.of(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "BUSINESS_RULE_VIOLATION",
                ex.getMessage() != null ? ex.getMessage() : "Operação não permitida no estado atual.",
                request.getRequestURI(),
                debugId
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        String debugId = LogContext.getDebugId();
        log.error("[UNHANDLED_EXCEPTION] debugId={} path={}", debugId, request.getRequestURI(), ex);

        ApiErrorResponse body = ApiErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                "Ocorreu um erro interno inesperado. Entre em contato com o suporte informando o debugId.",
                request.getRequestURI(),
                debugId
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
