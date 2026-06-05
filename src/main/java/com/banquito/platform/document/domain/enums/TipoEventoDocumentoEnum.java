package com.banquito.platform.document.domain.enums;

import lombok.Getter;

@Getter
public enum TipoEventoDocumentoEnum {
    REGISTRADO("REGISTRADO"),
    VERSION_REGISTRADA("VERSION_REGISTRADA"),
    CONSULTADO("CONSULTADO"),
    DESCARGADO("DESCARGADO"),
    ANULADO("ANULADO"),
    ARCHIVADO("ARCHIVADO"),
    REFERENCIA_ASOCIADA("REFERENCIA_ASOCIADA");

    private final String value;

    TipoEventoDocumentoEnum(String value) {
        this.value = value;
    }
}
