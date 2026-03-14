package com.ars.core.infrastructure.web.error;

import org.springframework.http.HttpStatus;

public class NotFoundException extends AppException {
    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, "BULUNAMADI", message);
    }
}
