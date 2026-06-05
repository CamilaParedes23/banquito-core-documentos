package com.banquito.platform.document.domain.enums;

import lombok.Getter;

@Getter
public enum EstadoDocumentoEnum {
    CREATED("CREATED"),
    STORED("STORED"),
    SIGNED("SIGNED"),
    ARCHIVED("ARCHIVED"),
    DELETED("DELETED"),
    FAILED("FAILED");

    private final String value;

    EstadoDocumentoEnum(String value) {
        this.value = value;
    }
}
