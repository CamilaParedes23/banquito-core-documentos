package com.banquito.platform.document.api.controller;

import com.banquito.platform.document.api.dto.api.DocumentMetadataResponse;
import com.banquito.platform.document.api.dto.api.RegisterDocumentRequest;
import com.banquito.platform.document.application.service.DocumentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/v1/documents")
public class InternalDocumentController {

    private final DocumentService documentService;

    public InternalDocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentMetadataResponse register(@Valid @RequestBody RegisterDocumentRequest request) {
        return documentService.registrarIdempotente(request);
    }
}
