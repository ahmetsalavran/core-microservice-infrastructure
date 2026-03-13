package com.microservice.infrastructure.web.error;

import org.springframework.http.HttpStatus;

public class BusinessRejectException extends AppException {
    public BusinessRejectException(String message) {
        super(HttpStatus.CONFLICT, "BUSINESS_REJECTED", message);
    }
}
