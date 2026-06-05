package com.banquito.platform.document.domain.repository;

import com.banquito.platform.document.domain.model.DocumentoEvento;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DocumentoEventoRepository extends MongoRepository<DocumentoEvento, String> {
    List<DocumentoEvento> findByUuidDocumentoOrderByFechaCreacionDesc(String uuidDocumento);
}
