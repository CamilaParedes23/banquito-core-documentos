package com.banquito.platform.document.infrastructure.grpc;

import com.banquito.platform.document.application.service.DocumentService;
import com.banquito.platform.document.shared.exception.BusinessException;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Component
public class DocumentGrpcService extends DocumentQueryServiceGrpc.DocumentQueryServiceImplBase {

    private final DocumentService documentService;
    private final ObjectMapper objectMapper;

    public DocumentGrpcService(DocumentService documentService, ObjectMapper objectMapper) {
        this.documentService = documentService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void registerDocument(RegisterDocumentRequest request,
                                 StreamObserver<com.banquito.platform.document.infrastructure.grpc.DocumentMetadataResponse> responseObserver) {
        try {
            validateRequired(request.getBusinessContext(), "DOCUMENT_BUSINESS_CONTEXT_REQUIRED", "El contexto de negocio es obligatorio");
            validateRequired(request.getDocumentType(), "DOCUMENT_TYPE_REQUIRED", "El tipo documental es obligatorio");
            validateRequired(request.getFileName(), "DOCUMENT_FILE_NAME_REQUIRED", "El nombre del archivo es obligatorio");
            validateRequired(request.getMimeType(), "DOCUMENT_MIME_TYPE_REQUIRED", "El tipo MIME es obligatorio");

            Map<String, Object> metadata = parseMap(request.getMetadataJson());
            var dto = new com.banquito.platform.document.api.dto.api.RegisterDocumentRequest(
                    request.getBusinessContext(),
                    request.getDocumentType(),
                    blankToNull(request.getBusinessReferenceUuid()),
                    request.getFileName(),
                    request.getMimeType(),
                    blankToNull(request.getStoragePath()),
                    blankToNull(request.getHashSha256()),
                    blankToNull(request.getContentBase64()),
                    blankToNull(request.getTextPayload()),
                    blankToNull(request.getCreatedBy()),
                    blankToNull(request.getCorrelationId()),
                    metadata
            );

            com.banquito.platform.document.api.dto.api.DocumentMetadataResponse response = documentService.registrarIdempotente(dto);
            responseObserver.onNext(toGrpc(response));
            responseObserver.onCompleted();
        } catch (Exception exception) {
            responseObserver.onError(toStatus(exception));
        }
    }

    @Override
    public void getDocumentMetadata(GetDocumentMetadataRequest request,
                                    StreamObserver<com.banquito.platform.document.infrastructure.grpc.DocumentMetadataResponse> responseObserver) {
        try {
            validateRequired(request.getDocumentUuid(), "DOCUMENT_UUID_REQUIRED", "El UUID del documento es obligatorio");
            responseObserver.onNext(toGrpc(documentService.obtener(request.getDocumentUuid())));
            responseObserver.onCompleted();
        } catch (Exception exception) {
            responseObserver.onError(toStatus(exception));
        }
    }

    @Override
    public void registerDocumentVersion(
            com.banquito.platform.document.infrastructure.grpc.RegisterDocumentVersionRequest request,
            StreamObserver<com.banquito.platform.document.infrastructure.grpc.DocumentVersionResponse> responseObserver) {
        try {
            validateRequired(request.getDocumentUuid(), "DOCUMENT_UUID_REQUIRED", "El UUID del documento es obligatorio");
            validateRequired(request.getFileName(), "DOCUMENT_FILE_NAME_REQUIRED", "El nombre del archivo es obligatorio");
            validateRequired(request.getMimeType(), "DOCUMENT_MIME_TYPE_REQUIRED", "El tipo MIME es obligatorio");

            com.banquito.platform.document.api.dto.api.RegisterDocumentVersionRequest dto = new com.banquito.platform.document.api.dto.api.RegisterDocumentVersionRequest(
                    request.getFileName(),
                    request.getMimeType(),
                    blankToNull(request.getStoragePath()),
                    blankToNull(request.getHashSha256()),
                    blankToNull(request.getContentBase64()),
                    blankToNull(request.getTextPayload()),
                    blankToNull(request.getCreatedBy())
            );
            responseObserver.onNext(toGrpc(documentService.registrarVersion(request.getDocumentUuid(), dto)));
            responseObserver.onCompleted();
        } catch (Exception exception) {
            responseObserver.onError(toStatus(exception));
        }
    }

    @Override
    public void attachDocumentToBusinessReference(
            AttachDocumentRequest request,
            StreamObserver<com.banquito.platform.document.infrastructure.grpc.DocumentMetadataResponse> responseObserver) {
        try {
            validateRequired(request.getDocumentUuid(), "DOCUMENT_UUID_REQUIRED", "El UUID del documento es obligatorio");
            validateRequired(request.getBusinessReferenceUuid(), "DOCUMENT_REFERENCE_REQUIRED", "La referencia de negocio es obligatoria");
            responseObserver.onNext(toGrpc(documentService.asociarReferencia(
                    request.getDocumentUuid(),
                    request.getBusinessReferenceUuid(),
                    blankToNull(request.getCorrelationId())
            )));
            responseObserver.onCompleted();
        } catch (Exception exception) {
            responseObserver.onError(toStatus(exception));
        }
    }

    private com.banquito.platform.document.infrastructure.grpc.DocumentMetadataResponse toGrpc(
            com.banquito.platform.document.api.dto.api.DocumentMetadataResponse response) throws Exception {
        return com.banquito.platform.document.infrastructure.grpc.DocumentMetadataResponse.newBuilder()
                .setDocumentUuid(nonNull(response.documentUuid()))
                .setBusinessContext(nonNull(response.businessContext()))
                .setDocumentType(nonNull(response.documentType()))
                .setBusinessReferenceUuid(nonNull(response.businessReferenceUuid()))
                .setFileName(nonNull(response.fileName()))
                .setMimeType(nonNull(response.mimeType()))
                .setStoragePath(nonNull(response.storagePath()))
                .setHashSha256(nonNull(response.hashSha256()))
                .setStatus(nonNull(response.status()))
                .setCreatedAt(response.createdAt() == null ? "" : response.createdAt().toString())
                .setCreatedBy(nonNull(response.createdBy()))
                .setCorrelationId(nonNull(response.correlationId()))
                .setMetadataJson(objectMapper.writeValueAsString(
                        response.metadata() == null ? Map.of() : response.metadata()))
                .build();
    }

    private com.banquito.platform.document.infrastructure.grpc.DocumentVersionResponse toGrpc(
            com.banquito.platform.document.api.dto.api.DocumentVersionResponse response) {
        return com.banquito.platform.document.infrastructure.grpc.DocumentVersionResponse.newBuilder()
                .setVersionUuid(nonNull(response.versionUuid()))
                .setDocumentUuid(nonNull(response.documentUuid()))
                .setVersionNumber(response.versionNumber() == null ? 0 : response.versionNumber())
                .setStatus(nonNull(response.status()))
                .setFileName(nonNull(response.fileName()))
                .setMimeType(nonNull(response.mimeType()))
                .setStoragePath(nonNull(response.storagePath()))
                .setHashSha256(nonNull(response.hashSha256()))
                .setCreatedAt(response.createdAt() == null ? "" : response.createdAt().toString())
                .setCreatedBy(nonNull(response.createdBy()))
                .build();
    }

    private Map<String, Object> parseMap(String json) throws Exception {
        if (json == null || json.isBlank()) {
            return Map.of();
        }
        return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() { });
    }

    private RuntimeException toStatus(Exception exception) {
        if (exception instanceof BusinessException businessException) {
            return mapStatus(businessException.getStatus())
                    .withDescription(businessException.getCode() + "|" + businessException.getMessage())
                    .withCause(exception)
                    .asRuntimeException();
        }
        return Status.INTERNAL
                .withDescription("DOCUMENT_GRPC_INTERNAL_ERROR|Error interno del servicio documental")
                .withCause(exception)
                .asRuntimeException();
    }

    private Status mapStatus(HttpStatus status) {
        if (status == HttpStatus.BAD_REQUEST || status == HttpStatus.UNPROCESSABLE_ENTITY) return Status.INVALID_ARGUMENT;
        if (status == HttpStatus.NOT_FOUND) return Status.NOT_FOUND;
        if (status == HttpStatus.CONFLICT) return Status.ALREADY_EXISTS;
        if (status == HttpStatus.UNAUTHORIZED) return Status.UNAUTHENTICATED;
        if (status == HttpStatus.FORBIDDEN) return Status.PERMISSION_DENIED;
        if (status == HttpStatus.SERVICE_UNAVAILABLE) return Status.UNAVAILABLE;
        return Status.INTERNAL;
    }

    private void validateRequired(String value, String code, String message) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(code, message, HttpStatus.BAD_REQUEST);
        }
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private String nonNull(String value) {
        return value == null ? "" : value;
    }
}
