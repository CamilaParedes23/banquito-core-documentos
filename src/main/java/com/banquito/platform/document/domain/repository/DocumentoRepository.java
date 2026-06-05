package com.banquito.platform.document.domain.repository;

import com.banquito.platform.document.domain.model.Documento;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DocumentoRepository extends MongoRepository<Documento, String> {
    Optional<Documento> findByUuidDocumento(String uuidDocumento);
    boolean existsByUuidDocumento(String uuidDocumento);
}
