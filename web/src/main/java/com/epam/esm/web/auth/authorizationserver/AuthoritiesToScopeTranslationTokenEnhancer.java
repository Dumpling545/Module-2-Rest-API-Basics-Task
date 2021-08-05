package com.epam.esm.web.auth.authorizationserver;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Token enhancer that includes {@link GrantedAuthority} objects bound to current authorization
 * request (application as authorization server) inside 'scopes' claim of JWT token.
 */
@Component
public class AuthoritiesToScopeTranslationTokenEnhancer implements TokenEnhancer {
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		var token = new DefaultOAuth2AccessToken(accessToken);
		var authorities = authentication.getUserAuthentication().getAuthorities();
		Set<String> scopes = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
		var requestedScopes = token.getScope();
		token.setScope(scopes.stream().filter(requestedScopes::contains).collect(Collectors.toSet()));
		return token;
	}
}
