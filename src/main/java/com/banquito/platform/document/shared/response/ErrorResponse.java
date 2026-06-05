package com.banquito.platform.document.shared.response;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        LocalDateTime timestamp,
        String correlationId,
        String code,
        String message,
        List<String> details
) {
}
