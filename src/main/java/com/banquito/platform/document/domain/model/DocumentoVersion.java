package com.banquito.platform.document.domain.model;

import com.banquito.platform.document.domain.enums.EstadoVersionDocumentoEnum;
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
@Document(collection = "document_versions")
public class DocumentoVersion {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("versionUuid")
    private String uuidVersion;

    @Indexed
    @Field("documentUuid")
    private String uuidDocumento;

    @Field("versionNumber")
    private Integer numeroVersion;

    @Field("fileName")
    private String nombreArchivo;

    @Field("mimeType")
    private String tipoMime;

    @Field("storagePath")
    private String rutaAlmacenamiento;

    @Field("hashSha256")
    private String hashSha256;

    @Field("status")
    private EstadoVersionDocumentoEnum estado;

    @Field("createdAt")
    private LocalDateTime fechaCreacion;

    @Field("createdBy")
    private String creadoPor;

    public DocumentoVersion() {
    }

    public DocumentoVersion(String id) {
        this.id = id;
    }

    public static DocumentoVersion crear(String uuidDocumento, Integer numeroVersion, String nombreArchivo,
                                         String tipoMime, String rutaAlmacenamiento, String hashSha256,
                                         String creadoPor) {
        DocumentoVersion version = new DocumentoVersion();
        version.uuidVersion = UUID.randomUUID().toString();
        version.uuidDocumento = uuidDocumento;
        version.numeroVersion = numeroVersion;
        version.nombreArchivo = nombreArchivo;
        version.tipoMime = tipoMime;
        version.rutaAlmacenamiento = rutaAlmacenamiento;
        version.hashSha256 = hashSha256;
        version.estado = EstadoVersionDocumentoEnum.ACTIVA;
        version.fechaCreacion = LocalDateTime.now();
        version.creadoPor = creadoPor;
        return version;
    }

    public void reemplazar() {
        this.estado = EstadoVersionDocumentoEnum.REEMPLAZADA;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentoVersion that)) return false;
        if (this.id == null || that.id == null) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "DocumentoVersion{" +
                "id='" + id + '\'' +
                ", uuidVersion='" + uuidVersion + '\'' +
                ", uuidDocumento='" + uuidDocumento + '\'' +
                ", numeroVersion=" + numeroVersion +
                ", estado=" + estado +
                '}';
    }
}
