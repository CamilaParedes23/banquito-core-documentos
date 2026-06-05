package com.banquito.platform.document.infrastructure.security;

import java.util.List;

public record JwtPrincipal(
        String subject,
        String username,
        String clientId,
        String actorType,
        List<String> roles,
        List<String> scopes
) {
}
