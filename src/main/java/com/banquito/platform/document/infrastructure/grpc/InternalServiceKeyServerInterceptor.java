package com.banquito.platform.document.infrastructure.grpc;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

@Component
public class InternalServiceKeyServerInterceptor implements ServerInterceptor {

    private static final Metadata.Key<String> INTERNAL_SERVICE_KEY_HEADER =
            Metadata.Key.of("x-internal-service-key", Metadata.ASCII_STRING_MARSHALLER);

    private final byte[] expectedKey;

    public InternalServiceKeyServerInterceptor(
            @Value("${banquito.internal.service-key}") String expectedKey) {
        this.expectedKey = expectedKey.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {
        String received = headers.get(INTERNAL_SERVICE_KEY_HEADER);
        if (received == null || !MessageDigest.isEqual(
                expectedKey,
                received.getBytes(StandardCharsets.UTF_8))) {
            call.close(
                    Status.UNAUTHENTICATED.withDescription(
                            "INTERNAL_SERVICE_UNAUTHENTICATED|Credencial interna inválida"),
                    new Metadata()
            );
            return new ServerCall.Listener<>() { };
        }
        return next.startCall(call, headers);
    }
}
