package com.banquito.platform.document.infrastructure.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class DocumentGrpcServer {

    private static final Logger log = LoggerFactory.getLogger(DocumentGrpcServer.class);

    private final int port;
    private final DocumentGrpcService documentGrpcService;
    private final InternalServiceKeyServerInterceptor internalServiceKeyInterceptor;
    private Server server;

    public DocumentGrpcServer(
            @Value("${banquito.grpc.server.port:9096}") int port,
            DocumentGrpcService documentGrpcService,
            InternalServiceKeyServerInterceptor internalServiceKeyInterceptor) {
        this.port = port;
        this.documentGrpcService = documentGrpcService;
        this.internalServiceKeyInterceptor = internalServiceKeyInterceptor;
    }

    @PostConstruct
    public void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(ServerInterceptors.intercept(
                        documentGrpcService,
                        internalServiceKeyInterceptor))
                .build()
                .start();
        log.info("Document gRPC server iniciado en puerto {}", port);
    }

    @PreDestroy
    public void stop() {
        if (server == null) return;
        server.shutdown();
        try {
            if (!server.awaitTermination(5, TimeUnit.SECONDS)) {
                server.shutdownNow();
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            server.shutdownNow();
        }
    }
}
