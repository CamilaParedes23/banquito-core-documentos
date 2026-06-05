package com.banquito.platform.document.domain.repository;

import com.banquito.platform.document.domain.model.DocumentoPayload;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DocumentoPayloadRepository extends MongoRepository<DocumentoPayload, String> {
    Optional<DocumentoPayload> findFirstByUuidDocumentoOrderByFechaCreacionDesc(String uuidDocumento);
    Optional<DocumentoPayload> findByUuidVersion(String uuidVersion);
}
