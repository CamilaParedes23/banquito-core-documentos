package com.banquito.platform.document.domain.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Document(collection = "document_payloads")
public class DocumentoPayload {

    @Id
    private String id;

    @Indexed
    @Field("documentUuid")
    private String uuidDocumento;

    @Indexed
    @Field("versionUuid")
    private String uuidVersion;

    @Field("contentBase64")
    private String contenidoBase64;

    @Field("textPayload")
    private String contenidoTexto;

    @Field("createdAt")
    private LocalDateTime fechaCreacion;

    public DocumentoPayload() {
    }

    public DocumentoPayload(String id) {
        this.id = id;
    }

    public static DocumentoPayload crear(String uuidDocumento, String uuidVersion, String contenidoBase64, String contenidoTexto) {
        DocumentoPayload payload = new DocumentoPayload();
        payload.uuidDocumento = uuidDocumento;
        payload.uuidVersion = uuidVersion;
        payload.contenidoBase64 = contenidoBase64;
        payload.contenidoTexto = contenidoTexto;
        payload.fechaCreacion = LocalDateTime.now();
        return payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentoPayload that)) return false;
        if (this.id == null || that.id == null) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "DocumentoPayload{" +
                "id='" + id + '\'' +
                ", uuidDocumento='" + uuidDocumento + '\'' +
                ", uuidVersion='" + uuidVersion + '\'' +
                '}';
    }
}
