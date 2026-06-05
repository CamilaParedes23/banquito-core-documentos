package com.banquito.platform.document.infrastructure.security;

import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {
    private final JwtProperties properties;
    private final ObjectMapper objectMapper;

    public JwtService(JwtProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    public JwtPrincipal validate(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Formato JWT inválido");
            }
            String signingInput = parts[0] + "." + parts[1];
            String expectedSignature = base64Url(hmacSha384(signingInput.getBytes(StandardCharsets.UTF_8)));
            if (!constantTimeEquals(expectedSignature, parts[2])) {
                throw new IllegalArgumentException("Firma JWT inválida");
            }
            byte[] payloadBytes = Base64.getUrlDecoder().decode(parts[1]);
            Map<String, Object> claims = objectMapper.readValue(payloadBytes, new TypeReference<Map<String, Object>>() {});

            Object exp = claims.get("exp");
            if (exp instanceof Number number && number.longValue() < Instant.now().getEpochSecond()) {
                throw new IllegalArgumentException("JWT expirado");
            }
            String issuer = String.valueOf(claims.get("iss"));
            if (properties.issuer() != null && !properties.issuer().equals(issuer)) {
                throw new IllegalArgumentException("JWT issuer inválido");
            }
            String subject = String.valueOf(claims.get("sub"));
            String username = claims.get("username") != null ? String.valueOf(claims.get("username")) : null;
            String actorType = claims.get("actorType") != null ? String.valueOf(claims.get("actorType")) : null;
            String clientId = claims.get("clientId") != null ? String.valueOf(claims.get("clientId")) : null;
            List<String> roles = asStringList(claims.get("roles"));
            List<String> scopes = asStringList(claims.get("scopes"));
            return new JwtPrincipal(subject, username, clientId, actorType, roles, scopes);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Token inválido", ex);
        }
    }

    private List<String> asStringList(Object value) {
        if (value instanceof List<?> list) {
            return list.stream().map(String::valueOf).toList();
        }
        return List.of();
    }

    private byte[] hmacSha384(byte[] data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA384");
        mac.init(new SecretKeySpec(properties.secret().getBytes(StandardCharsets.UTF_8), "HmacSHA384"));
        return mac.doFinal(data);
    }

    private String base64Url(byte[] data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null || a.length() != b.length()) return false;
        int result = 0;
        for (int i = 0; i < a.length(); i++) result |= a.charAt(i) ^ b.charAt(i);
        return result == 0;
    }
}
