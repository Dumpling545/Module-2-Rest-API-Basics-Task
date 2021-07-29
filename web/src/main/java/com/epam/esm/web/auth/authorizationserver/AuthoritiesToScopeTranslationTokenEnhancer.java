package com.epam.esm.web.auth.authorizationserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AuthoritiesToScopeTranslationTokenEnhancer implements TokenEnhancer {
    //TODO delete
    private static final Logger logger = LoggerFactory.getLogger(AuthoritiesToScopeTranslationTokenEnhancer.class);
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        var token = new DefaultOAuth2AccessToken(accessToken);
        var authorities = authentication.getUserAuthentication().getAuthorities();
        Set<String> scopes = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        logger.info("scopes={}", scopes.toString());
        var requestedScopes = token.getScope();
        logger.info("requestedScopes={}", requestedScopes.toString());
        token.setScope(scopes.stream().filter(requestedScopes::contains).collect(Collectors.toSet()));
        logger.info("result={}", token.getScope().toString());
        return token;
    }
}
