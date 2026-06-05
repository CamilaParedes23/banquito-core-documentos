package com.banquito.platform.document.domain.repository;

import com.banquito.platform.document.domain.model.DocumentoVersion;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentoVersionRepository extends MongoRepository<DocumentoVersion, String> {
    Optional<DocumentoVersion> findByUuidVersion(String uuidVersion);
    List<DocumentoVersion> findByUuidDocumentoOrderByNumeroVersionDesc(String uuidDocumento);
    long countByUuidDocumento(String uuidDocumento);
}
