package com.banquito.platform.document.application.service;

import com.banquito.platform.document.api.dto.api.*;
import com.banquito.platform.document.domain.enums.EstadoTipoDocumentoEnum;
import com.banquito.platform.document.domain.enums.TipoEventoDocumentoEnum;
import com.banquito.platform.document.domain.model.*;
import com.banquito.platform.document.domain.repository.*;
import com.banquito.platform.document.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class DocumentService {
    private final DocumentoRepository documentoRepository;
    private final DocumentoVersionRepository versionRepository;
    private final DocumentoPayloadRepository payloadRepository;
    private final DocumentoEventoRepository eventoRepository;
    private final TipoDocumentoCatalogoRepository tipoRepository;

    public DocumentService(DocumentoRepository documentoRepository,
                           DocumentoVersionRepository versionRepository,
                           DocumentoPayloadRepository payloadRepository,
                           DocumentoEventoRepository eventoRepository,
                           TipoDocumentoCatalogoRepository tipoRepository) {
        this.documentoRepository = documentoRepository;
        this.versionRepository = versionRepository;
        this.payloadRepository = payloadRepository;
        this.eventoRepository = eventoRepository;
        this.tipoRepository = tipoRepository;
    }

    public List<DocumentTypeResponse> listarTipos() {
        return tipoRepository.findByStatusOrderByNameAsc(EstadoTipoDocumentoEnum.ACTIVO)
                .stream().map(this::toTypeResponse).toList();
    }

    @Transactional
    public DocumentMetadataResponse registrar(RegisterDocumentRequest request) {
        validarTipoDocumento(request.documentType());
        Documento documento = Documento.registrar(
                request.businessContext(), request.documentType(), request.businessReferenceUuid(),
                request.fileName(), request.mimeType(), request.storagePath(), request.hashSha256(),
                request.createdBy(), request.correlationId(), request.metadata()
        );
        documento.marcarVigente();
        Documento saved = documentoRepository.save(documento);
        DocumentoVersion version = DocumentoVersion.crear(saved.getUuidDocumento(), 1, request.fileName(), request.mimeType(),
                request.storagePath(), request.hashSha256(), request.createdBy());
        DocumentoVersion savedVersion = versionRepository.save(version);
        if (request.contentBase64() != null || request.textPayload() != null) {
            payloadRepository.save(DocumentoPayload.crear(saved.getUuidDocumento(), savedVersion.getUuidVersion(),
                    request.contentBase64(), request.textPayload()));
        }
        eventoRepository.save(DocumentoEvento.crear(saved.getUuidDocumento(), TipoEventoDocumentoEnum.REGISTRADO,
                "Documento registrado", request.createdBy(), request.correlationId()));
        return toMetadataResponse(saved);
    }

    @Transactional
    public DocumentMetadataResponse registrarIdempotente(RegisterDocumentRequest request) {
        if (request.businessReferenceUuid() != null && !request.businessReferenceUuid().isBlank()) {
            return documentoRepository
                    .findFirstByContextoNegocioAndTipoDocumentoAndUuidReferenciaNegocio(
                            request.businessContext(),
                            request.documentType(),
                            request.businessReferenceUuid()
                    )
                    .map(this::toMetadataResponse)
                    .orElseGet(() -> registrar(request));
        }
        return registrar(request);
    }

    public DocumentMetadataResponse obtener(String documentUuid) {
        return toMetadataResponse(buscarDocumento(documentUuid));
    }

    @Transactional
    public DocumentMetadataResponse asociarReferencia(String documentUuid,
                                                       String businessReferenceUuid,
                                                       String correlationId) {
        Documento documento = buscarDocumento(documentUuid);
        documento.setUuidReferenciaNegocio(businessReferenceUuid);
        if (correlationId != null && !correlationId.isBlank()) {
            documento.setCorrelationId(correlationId);
        }
        Documento saved = documentoRepository.save(documento);
        eventoRepository.save(DocumentoEvento.crear(
                documentUuid,
                TipoEventoDocumentoEnum.REFERENCIA_ASOCIADA,
                "Referencia de negocio asociada",
                "document-service",
                correlationId
        ));
        return toMetadataResponse(saved);
    }

    public List<DocumentMetadataResponse> buscar(String context, String type, String businessReferenceUuid) {
        return documentoRepository.findAll().stream()
                .filter(documento -> context == null || context.isBlank() || Objects.equals(documento.getContextoNegocio(), context))
                .filter(documento -> type == null || type.isBlank() || Objects.equals(documento.getTipoDocumento(), type))
                .filter(documento -> businessReferenceUuid == null || businessReferenceUuid.isBlank() || Objects.equals(documento.getUuidReferenciaNegocio(), businessReferenceUuid))
                .sorted(Comparator.comparing(Documento::getFechaCreacion).reversed())
                .map(this::toMetadataResponse)
                .toList();
    }

    public DocumentPayloadResponse descargar(String documentUuid) {
        buscarDocumento(documentUuid);
        DocumentoPayload payload = payloadRepository.findFirstByUuidDocumentoOrderByFechaCreacionDesc(documentUuid)
                .orElseThrow(() -> new BusinessException("DOCUMENT_PAYLOAD_NOT_FOUND", "No existe payload registrado para el documento", HttpStatus.NOT_FOUND));
        return new DocumentPayloadResponse(payload.getUuidDocumento(), payload.getUuidVersion(), payload.getContenidoBase64(),
                payload.getContenidoTexto(), payload.getFechaCreacion());
    }

    @Transactional
    public DocumentVersionResponse registrarVersion(String documentUuid, RegisterDocumentVersionRequest request) {
        Documento documento = buscarDocumento(documentUuid);
        List<DocumentoVersion> anteriores = versionRepository.findByUuidDocumentoOrderByNumeroVersionDesc(documentUuid);
        anteriores.stream().filter(v -> "ACTIVA".equals(v.getEstado().getValue())).forEach(DocumentoVersion::reemplazar);
        versionRepository.saveAll(anteriores);
        int nextVersion = anteriores.stream().map(DocumentoVersion::getNumeroVersion).max(Integer::compareTo).orElse(0) + 1;
        DocumentoVersion version = DocumentoVersion.crear(documentUuid, nextVersion, request.fileName(), request.mimeType(),
                request.storagePath(), request.hashSha256(), request.createdBy());
        DocumentoVersion saved = versionRepository.save(version);
        documento.setNombreArchivo(request.fileName());
        documento.setTipoMime(request.mimeType());
        documento.setRutaAlmacenamiento(request.storagePath());
        documento.setHashSha256(request.hashSha256());
        documentoRepository.save(documento);
        if (request.contentBase64() != null || request.textPayload() != null) {
            payloadRepository.save(DocumentoPayload.crear(documentUuid, saved.getUuidVersion(), request.contentBase64(), request.textPayload()));
        }
        eventoRepository.save(DocumentoEvento.crear(documentUuid, TipoEventoDocumentoEnum.VERSION_REGISTRADA,
                "Versión " + nextVersion + " registrada", request.createdBy(), null));
        return toVersionResponse(saved);
    }

    public List<DocumentVersionResponse> listarVersiones(String documentUuid) {
        buscarDocumento(documentUuid);
        return versionRepository.findByUuidDocumentoOrderByNumeroVersionDesc(documentUuid).stream()
                .map(this::toVersionResponse).toList();
    }

    @Transactional
    public DocumentEventResponse registrarEvento(String documentUuid, RegisterDocumentEventRequest request) {
        buscarDocumento(documentUuid);
        DocumentoEvento evento = DocumentoEvento.crear(documentUuid, request.eventType(), request.detail(), request.actorUuid(), request.correlationId());
        return toEventResponse(eventoRepository.save(evento));
    }

    public List<DocumentEventResponse> listarEventos(String documentUuid) {
        buscarDocumento(documentUuid);
        return eventoRepository.findByUuidDocumentoOrderByFechaCreacionDesc(documentUuid).stream()
                .map(this::toEventResponse).toList();
    }

    private Documento buscarDocumento(String documentUuid) {
        return documentoRepository.findByUuidDocumento(documentUuid)
                .orElseThrow(() -> new BusinessException("DOCUMENT_NOT_FOUND", "Documento no encontrado", HttpStatus.NOT_FOUND));
    }

    private void validarTipoDocumento(String code) {
        tipoRepository.findByCode(code)
                .filter(tipo -> tipo.getStatus() == EstadoTipoDocumentoEnum.ACTIVO)
                .orElseThrow(() -> new BusinessException("DOCUMENT_TYPE_NOT_FOUND", "Tipo documental no encontrado o inactivo", HttpStatus.UNPROCESSABLE_ENTITY));
    }

    private DocumentMetadataResponse toMetadataResponse(Documento d) {
        return new DocumentMetadataResponse(d.getUuidDocumento(), d.getContextoNegocio(), d.getTipoDocumento(),
                d.getUuidReferenciaNegocio(), d.getNombreArchivo(), d.getTipoMime(), d.getRutaAlmacenamiento(),
                d.getHashSha256(), d.getEstado().getValue(), d.getFechaCreacion(), d.getCreadoPor(), d.getCorrelationId(), d.getMetadata());
    }

    private DocumentVersionResponse toVersionResponse(DocumentoVersion v) {
        return new DocumentVersionResponse(v.getUuidVersion(), v.getUuidDocumento(), v.getNumeroVersion(),
                v.getNombreArchivo(), v.getTipoMime(), v.getRutaAlmacenamiento(), v.getHashSha256(),
                v.getEstado().getValue(), v.getFechaCreacion(), v.getCreadoPor());
    }

    private DocumentEventResponse toEventResponse(DocumentoEvento e) {
        return new DocumentEventResponse(e.getUuidEvento(), e.getUuidDocumento(), e.getTipoEvento().getValue(),
                e.getDetalle(), e.getActorUuid(), e.getFechaCreacion(), e.getCorrelationId());
    }

    private DocumentTypeResponse toTypeResponse(TipoDocumentoCatalogo t) {
        return new DocumentTypeResponse(t.getCode(), t.getName(), t.getDescription(), t.getOwnerService(), t.getStatus().getValue());
    }
}
