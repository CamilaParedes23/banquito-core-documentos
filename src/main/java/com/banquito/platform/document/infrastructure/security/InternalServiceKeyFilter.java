package com.banquito.platform.document.infrastructure.security;

import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Component
public class InternalServiceKeyFilter extends OncePerRequestFilter {

    private final String expectedKey;
    private final ObjectMapper objectMapper;

    public InternalServiceKeyFilter(
            @Value("${banquito.internal.service-key}") String expectedKey,
            ObjectMapper objectMapper) {
        this.expectedKey = expectedKey;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/internal/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String providedKey = request.getHeader("X-Internal-Service-Key");
        if (expectedKey != null && expectedKey.equals(providedKey)) {
            filterChain.doFilter(request, response);
            return;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "code", "INTERNAL_SERVICE_UNAUTHORIZED",
                "message", "Credencial interna inválida"
        ));
    }
}
