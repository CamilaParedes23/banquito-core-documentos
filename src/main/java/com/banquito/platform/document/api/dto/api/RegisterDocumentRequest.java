package com.banquito.platform.document.api.dto.api;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

public record RegisterDocumentRequest(
        @NotBlank String businessContext,
        @NotBlank String documentType,
        String businessReferenceUuid,
        @NotBlank String fileName,
        @NotBlank String mimeType,
        String storagePath,
        String hashSha256,
        String contentBase64,
        String textPayload,
        String createdBy,
        String correlationId,
        Map<String, Object> metadata
) {
}
