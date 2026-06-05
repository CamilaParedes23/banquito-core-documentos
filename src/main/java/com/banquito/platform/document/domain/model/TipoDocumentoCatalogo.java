package com.banquito.platform.document.domain.model;

import com.banquito.platform.document.domain.enums.EstadoTipoDocumentoEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Objects;

@Getter
@Setter
@Document(collection = "document_type_catalog")
public class TipoDocumentoCatalogo {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("code")
    private String code;

    @Field("name")
    private String name;

    @Field("description")
    private String description;

    @Field("ownerService")
    private String ownerService;

    @Field("status")
    private EstadoTipoDocumentoEnum status;

    public TipoDocumentoCatalogo() {
    }

    public TipoDocumentoCatalogo(String id) {
        this.id = id;
    }

    public static TipoDocumentoCatalogo crear(String code, String name, String description, String ownerService) {
        TipoDocumentoCatalogo tipo = new TipoDocumentoCatalogo();
        tipo.code = code;
        tipo.name = name;
        tipo.description = description;
        tipo.ownerService = ownerService;
        tipo.status = EstadoTipoDocumentoEnum.ACTIVO;
        return tipo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TipoDocumentoCatalogo that)) return false;
        if (this.id == null || that.id == null) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "TipoDocumentoCatalogo{" +
                "id='" + id + '\'' +
                ", code='" + code + '\'' +
                ", ownerService='" + ownerService + '\'' +
                ", status=" + status +
                '}';
    }
}
