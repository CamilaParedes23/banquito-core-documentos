package com.banquito.platform.document.domain.model;

import com.banquito.platform.document.domain.enums.TipoEventoDocumentoEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Document(collection = "document_events")
public class DocumentoEvento {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("eventUuid")
    private String uuidEvento;

    @Indexed
    @Field("documentUuid")
    private String uuidDocumento;

    @Field("eventType")
    private TipoEventoDocumentoEnum tipoEvento;

    @Field("detail")
    private String detalle;

    @Field("actorUuid")
    private String actorUuid;

    @Field("createdAt")
    private LocalDateTime fechaCreacion;

    @Field("correlationId")
    private String correlationId;

    public DocumentoEvento() {
    }

    public DocumentoEvento(String id) {
        this.id = id;
    }

    public static DocumentoEvento crear(String uuidDocumento, TipoEventoDocumentoEnum tipoEvento,
                                        String detalle, String actorUuid, String correlationId) {
        DocumentoEvento evento = new DocumentoEvento();
        evento.uuidEvento = UUID.randomUUID().toString();
        evento.uuidDocumento = uuidDocumento;
        evento.tipoEvento = tipoEvento;
        evento.detalle = detalle;
        evento.actorUuid = actorUuid;
        evento.fechaCreacion = LocalDateTime.now();
        evento.correlationId = correlationId;
        return evento;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentoEvento that)) return false;
        if (this.id == null || that.id == null) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "DocumentoEvento{" +
                "id='" + id + '\'' +
                ", uuidEvento='" + uuidEvento + '\'' +
                ", uuidDocumento='" + uuidDocumento + '\'' +
                ", tipoEvento=" + tipoEvento +
                '}';
    }
}
