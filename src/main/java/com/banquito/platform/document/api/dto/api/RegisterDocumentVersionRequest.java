package com.banquito.platform.document.api.dto.api;

import jakarta.validation.constraints.NotBlank;

public record RegisterDocumentVersionRequest(
        @NotBlank String fileName,
        @NotBlank String mimeType,
        String storagePath,
        String hashSha256,
        String contentBase64,
        String textPayload,
        String createdBy
) {
}
