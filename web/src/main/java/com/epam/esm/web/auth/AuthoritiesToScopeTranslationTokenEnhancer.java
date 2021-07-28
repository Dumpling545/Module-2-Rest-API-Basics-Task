package com.epam.esm.web.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AuthoritiesToScopeTranslationTokenEnhancer implements TokenEnhancer {
    private static final Logger logger = LoggerFactory.getLogger(AuthoritiesToScopeTranslationTokenEnhancer.class);
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        var token = new DefaultOAuth2AccessToken(accessToken);
        var authorities = authentication.getUserAuthentication().getAuthorities();
        Set<String> scopes = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        token.setScope(scopes);
        return token;
    }
}
