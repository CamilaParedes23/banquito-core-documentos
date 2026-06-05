package com.banquito.platform.document.api.dto.api;

import java.time.LocalDateTime;

public record DocumentEventResponse(
        String eventUuid,
        String documentUuid,
        String eventType,
        String detail,
        String actorUuid,
        LocalDateTime createdAt,
        String correlationId
) {
}
