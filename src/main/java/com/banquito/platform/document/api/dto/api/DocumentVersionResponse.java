package com.banquito.platform.document.api.dto.api;

import java.time.LocalDateTime;

public record DocumentVersionResponse(
        String versionUuid,
        String documentUuid,
        Integer versionNumber,
        String fileName,
        String mimeType,
        String storagePath,
        String hashSha256,
        String status,
        LocalDateTime createdAt,
        String createdBy
) {
}
