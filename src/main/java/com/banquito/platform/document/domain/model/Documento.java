package com.banquito.platform.document.domain.model;

import com.banquito.platform.document.domain.enums.EstadoDocumentoEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Document(collection = "documents")
public class Documento {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("documentUuid")
    private String uuidDocumento;

    @Indexed
    @Field("businessContext")
    private String contextoNegocio;

    @Indexed
    @Field("documentType")
    private String tipoDocumento;

    @Indexed
    @Field("businessReferenceUuid")
    private String uuidReferenciaNegocio;

    @Field("fileName")
    private String nombreArchivo;

    @Field("mimeType")
    private String tipoMime;

    @Field("storagePath")
    private String rutaAlmacenamiento;

    @Field("hashSha256")
    private String hashSha256;

    @Field("status")
    private EstadoDocumentoEnum estado;

    @Field("createdAt")
    private LocalDateTime fechaCreacion;

    @Field("createdBy")
    private String creadoPor;

    @Field("correlationId")
    private String correlationId;

    @Field("metadata")
    private Map<String, Object> metadata = new LinkedHashMap<>();

    public Documento() {
    }

    public Documento(String id) {
        this.id = id;
    }

    public static Documento registrar(String contextoNegocio, String tipoDocumento, String uuidReferenciaNegocio,
                                      String nombreArchivo, String tipoMime, String rutaAlmacenamiento,
                                      String hashSha256, String creadoPor, String correlationId,
                                      Map<String, Object> metadata) {
        Documento documento = new Documento();
        documento.uuidDocumento = UUID.randomUUID().toString();
        documento.contextoNegocio = contextoNegocio;
        documento.tipoDocumento = tipoDocumento;
        documento.uuidReferenciaNegocio = uuidReferenciaNegocio;
        documento.nombreArchivo = nombreArchivo;
        documento.tipoMime = tipoMime;
        documento.rutaAlmacenamiento = rutaAlmacenamiento;
        documento.hashSha256 = hashSha256;
        documento.estado = EstadoDocumentoEnum.CREATED;
        documento.fechaCreacion = LocalDateTime.now();
        documento.creadoPor = creadoPor;
        documento.correlationId = correlationId;
        if (metadata != null) {
            documento.metadata = new LinkedHashMap<>(metadata);
        }
        return documento;
    }

    public void marcarVigente() {
        this.estado = EstadoDocumentoEnum.STORED;
    }

    public void anular() {
        this.estado = EstadoDocumentoEnum.DELETED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Documento that)) return false;
        if (this.id == null || that.id == null) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Documento{" +
                "id='" + id + '\'' +
                ", uuidDocumento='" + uuidDocumento + '\'' +
                ", contextoNegocio='" + contextoNegocio + '\'' +
                ", tipoDocumento='" + tipoDocumento + '\'' +
                ", estado=" + estado +
                '}';
    }
}
