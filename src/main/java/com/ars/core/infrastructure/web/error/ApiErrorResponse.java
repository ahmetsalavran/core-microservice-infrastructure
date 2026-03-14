package com.ars.core.infrastructure.web.error;

import java.time.OffsetDateTime;
import java.util.Map;

public record ApiErrorResponse(
        OffsetDateTime timestamp,
        int status,
        String error,
        String code,
        String message,
        String path,
        String requestId,
        Map<String, String> fieldErrors
) {
    public static ApiErrorResponse of(
            int status,
            String error,
            String code,
            String message,
            String path,
            String requestId,
            Map<String, String> fieldErrors
    ) {
        return new ApiErrorResponse(
                OffsetDateTime.now(),
                status,
                error,
                code,
                message,
                path,
                requestId,
                fieldErrors
        );
    }
}
