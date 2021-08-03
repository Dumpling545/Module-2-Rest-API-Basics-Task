package com.epam.esm.web.auth.authorizationserver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Extension of {@link DefaultUserAuthenticationConverter} that overrides logic about
 * what kind of user information is going to be inside converted jwt token (application as authorization server).
 */
@Component
public class IgnoreAuthoritiesUserAuthenticationConverter extends DefaultUserAuthenticationConverter {
	@Value("${oauth2.claims.user-id}")
	private String userIdClaim;

	@Override
	public Map<String, ?> convertUserAuthentication(Authentication authentication) {
		return Map.of(userIdClaim, authentication.getDetails());
	}
}

