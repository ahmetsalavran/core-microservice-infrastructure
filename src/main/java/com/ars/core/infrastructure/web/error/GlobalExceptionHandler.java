package com.ars.core.infrastructure.web.error;

import com.ars.core.infrastructure.web.logging.RequestIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final Map<HttpStatus, String> STATUS_TEXTS = Map.of(
            HttpStatus.BAD_REQUEST, "Geçersiz İstek",
            HttpStatus.NOT_FOUND, "Bulunamadı",
            HttpStatus.CONFLICT, "Çakışma",
            HttpStatus.INTERNAL_SERVER_ERROR, "Sunucu Hatası"
    );

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiErrorResponse> handleAppException(AppException ex, HttpServletRequest request) {
        HttpStatus status = ex.getStatus();
        logByStatus(status, ex, request);
        return ResponseEntity.status(status)
                .body(ApiErrorResponse.of(
                        status.value(),
                        durumMetni(status),
                        ex.getCode(),
                        ex.getMessage(),
                        request.getRequestURI(),
                        requestId(request),
                        null
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() == null ? "invalid" : fe.getDefaultMessage(),
                        (a, b) -> a
                ));

        log.warn("İstek doğrulama hatası. requestId={} path={} errors={}",
                requestId(request), request.getRequestURI(), fieldErrors);

        return ResponseEntity.badRequest()
                .body(ApiErrorResponse.of(
                        HttpStatus.BAD_REQUEST.value(),
                        durumMetni(HttpStatus.BAD_REQUEST),
                        "DOGRULAMA_HATASI",
                        "Doğrulama hatası.",
                        request.getRequestURI(),
                        requestId(request),
                        fieldErrors
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Geçersiz argüman. requestId={} path={} message={}",
                requestId(request), request.getRequestURI(), ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiErrorResponse.of(
                        HttpStatus.BAD_REQUEST.value(),
                        durumMetni(HttpStatus.BAD_REQUEST),
                        "GECERSIZ_ISTEK",
                        ex.getMessage(),
                        request.getRequestURI(),
                        requestId(request),
                        null
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnhandled(Exception ex, HttpServletRequest request) {
        log.error("Beklenmeyen hata. requestId={} path={} message={}",
                requestId(request), request.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorResponse.of(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        durumMetni(HttpStatus.INTERNAL_SERVER_ERROR),
                        "SUNUCU_HATASI",
                        "Beklenmeyen sunucu hatası.",
                        request.getRequestURI(),
                        requestId(request),
                        null
                ));
    }

    private String requestId(HttpServletRequest request) {
        Object value = request.getAttribute(RequestIdFilter.MDC_REQUEST_ID_KEY);
        return value == null ? null : value.toString();
    }

    private void logByStatus(HttpStatus status, AppException ex, HttpServletRequest request) {
        if (status.is5xxServerError()) {
            log.error("İstek hatası. requestId={} path={} code={} message={}",
                    requestId(request), request.getRequestURI(), ex.getCode(), ex.getMessage(), ex);
            return;
        }
        log.warn("İstek hatası. requestId={} path={} code={} message={}",
                requestId(request), request.getRequestURI(), ex.getCode(), ex.getMessage());
    }

    private String durumMetni(HttpStatus status) {
        return STATUS_TEXTS.getOrDefault(status, "Hata");
    }
}
