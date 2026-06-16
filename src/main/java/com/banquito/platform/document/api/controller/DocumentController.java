package com.banquito.platform.document.api.controller;

import com.banquito.platform.document.api.dto.api.*;
import com.banquito.platform.document.application.service.DocumentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/documents")
@PreAuthorize("hasAnyRole('ADMIN_SEGURIDAD', 'OPERADOR_CONTABLE') or hasAuthority('SCOPE_document.create')")
public class DocumentController {
    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/types")
    public List<DocumentTypeResponse> listTypes() {
        return documentService.listarTipos();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentMetadataResponse register(@Valid @RequestBody RegisterDocumentRequest request) {
        return documentService.registrar(request);
    }

    @GetMapping("/{documentUuid}")
    public DocumentMetadataResponse get(@PathVariable String documentUuid) {
        return documentService.obtener(documentUuid);
    }

    @GetMapping
    public List<DocumentMetadataResponse> search(@RequestParam(required = false) String context,
                                                 @RequestParam(required = false) String type,
                                                 @RequestParam(required = false) String businessReferenceUuid) {
        return documentService.buscar(context, type, businessReferenceUuid);
    }

    @GetMapping("/{documentUuid}/download")
    public DocumentPayloadResponse download(@PathVariable String documentUuid) {
        return documentService.descargar(documentUuid);
    }

    @PostMapping("/{documentUuid}/versions")
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentVersionResponse registerVersion(@PathVariable String documentUuid,
                                                   @Valid @RequestBody RegisterDocumentVersionRequest request) {
        return documentService.registrarVersion(documentUuid, request);
    }

    @GetMapping("/{documentUuid}/versions")
    public List<DocumentVersionResponse> listVersions(@PathVariable String documentUuid) {
        return documentService.listarVersiones(documentUuid);
    }

    @PostMapping("/{documentUuid}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentEventResponse registerEvent(@PathVariable String documentUuid,
                                               @Valid @RequestBody RegisterDocumentEventRequest request) {
        return documentService.registrarEvento(documentUuid, request);
    }

    @GetMapping("/{documentUuid}/events")
    public List<DocumentEventResponse> listEvents(@PathVariable String documentUuid) {
        return documentService.listarEventos(documentUuid);
    }
}
