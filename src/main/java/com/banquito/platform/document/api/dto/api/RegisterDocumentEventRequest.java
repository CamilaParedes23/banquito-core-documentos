package com.banquito.platform.document.api.dto.api;

import com.banquito.platform.document.domain.enums.TipoEventoDocumentoEnum;
import jakarta.validation.constraints.NotNull;

public record RegisterDocumentEventRequest(
        @NotNull TipoEventoDocumentoEnum eventType,
        String detail,
        String actorUuid,
        String correlationId
) {
}
