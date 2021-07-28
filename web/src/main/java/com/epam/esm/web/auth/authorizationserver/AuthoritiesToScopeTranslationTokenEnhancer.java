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
    private static final Logger logger = LoggerFactory.getLogger(AuthoritiesToScopeTranslationTokenEnhancer.class);
    @Value("${oauth2.scopes.request-all-possible-scopes}")
    private String allPossibleScopes;
    //TODO make as property
    private String invalidScopeMessage = "One or more scopes from requested scope list(%s) cannot be granted to user with id=%d";
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        var token = new DefaultOAuth2AccessToken(accessToken);
        var authorities = authentication.getUserAuthentication().getAuthorities();
        Set<String> scopes = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        var requestedScopes = token.getScope();
        if(requestedScopes.contains(allPossibleScopes)){
            token.setScope(scopes);
        } else if(!scopes.containsAll(requestedScopes)){
            var message = String.format(invalidScopeMessage,
                    requestedScopes.toString(),
                    authentication.getUserAuthentication().getDetails());
            throw new InvalidScopeException(message);
        } else {
            token.setScope(scopes.stream().filter(requestedScopes::contains).collect(Collectors.toSet()));
        }
        return token;
    }
}
