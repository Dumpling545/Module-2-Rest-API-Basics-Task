package com.epam.esm.web.auth.authorizationserver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class IgnoreAuthoritiesUserAuthenticationConverter extends DefaultUserAuthenticationConverter {
    @Value("${oauth2.claims.user-id}")
    private String userIdClaim;
    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        return Map.of(userIdClaim, authentication.getDetails());
    }
}

