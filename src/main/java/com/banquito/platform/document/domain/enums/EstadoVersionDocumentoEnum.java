package com.banquito.platform.document.domain.enums;

import lombok.Getter;

@Getter
public enum EstadoVersionDocumentoEnum {
    ACTIVA("ACTIVA"),
    REEMPLAZADA("REEMPLAZADA"),
    ANULADA("ANULADA");

    private final String value;

    EstadoVersionDocumentoEnum(String value) {
        this.value = value;
    }
}
