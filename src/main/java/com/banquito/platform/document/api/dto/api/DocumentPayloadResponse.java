package com.banquito.platform.document.api.dto.api;

import java.time.LocalDateTime;

public record DocumentPayloadResponse(
        String documentUuid,
        String versionUuid,
        String contentBase64,
        String textPayload,
        LocalDateTime createdAt
) {
}
