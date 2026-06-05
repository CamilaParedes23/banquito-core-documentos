package com.banquito.platform.document.domain.enums;

import lombok.Getter;

@Getter
public enum EstadoTipoDocumentoEnum {
    ACTIVO("ACTIVO"),
    INACTIVO("INACTIVO");

    private final String value;

    EstadoTipoDocumentoEnum(String value) {
        this.value = value;
    }
}
