package com.banquito.platform.document.domain.repository;

import com.banquito.platform.document.domain.enums.EstadoTipoDocumentoEnum;
import com.banquito.platform.document.domain.model.TipoDocumentoCatalogo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TipoDocumentoCatalogoRepository extends MongoRepository<TipoDocumentoCatalogo, String> {
    Optional<TipoDocumentoCatalogo> findByCode(String code);
    boolean existsByCode(String code);
    List<TipoDocumentoCatalogo> findByStatusOrderByNameAsc(EstadoTipoDocumentoEnum status);
}
