package com.banquito.platform.document.api.dto.api;

import java.time.LocalDateTime;
import java.util.Map;

public record DocumentMetadataResponse(
        String documentUuid,
        String businessContext,
        String documentType,
        String businessReferenceUuid,
        String fileName,
        String mimeType,
        String storagePath,
        String hashSha256,
        String status,
        LocalDateTime createdAt,
        String createdBy,
        String correlationId,
        Map<String, Object> metadata
) {
}
