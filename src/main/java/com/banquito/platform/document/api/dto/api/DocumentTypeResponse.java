package com.banquito.platform.document.api.dto.api;

public record DocumentTypeResponse(
        String code,
        String name,
        String description,
        String ownerService,
        String status
) {
}
