package com.epam.esm;

import com.epam.esm.web.auth.AuthoritiesToScopeTranslationTokenEnhancer;
import com.epam.esm.web.auth.IgnoreAuthoritiesUserAuthenticationConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.List;

/**
 * NOTE: Deprecated class is used since there is no current support for Implicit and Password owner flows at
 * Authorization Server side
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Value("${oauth2.resource-server.jwt.key-value}")
    private String jwtKey;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AuthoritiesToScopeTranslationTokenEnhancer authoritiesToScopeTranslationTokenEnhancer;
    @Autowired
    private IgnoreAuthoritiesUserAuthenticationConverter ignoreAuthoritiesUserAuthenticationConverter;

    //TODO remove in-memory
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("client")
                .secret("{noop}secret")
                .authorizedGrantTypes("password", "implicit")
                .scopes("USER", "ADMIN")
                .redirectUris("example.com");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints
                .authenticationManager(authenticationManager)
                .tokenStore(tokenStore())
                .tokenEnhancer(tokenEnhancerChain());
    }

    @Bean
    public TokenEnhancerChain tokenEnhancerChain() {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> enhancers = List.of(authoritiesToScopeTranslationTokenEnhancer, jwtAccessTokenConverter());
        tokenEnhancerChain.setTokenEnhancers(enhancers);
        return tokenEnhancerChain;
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        DefaultAccessTokenConverter tokenConverter = new DefaultAccessTokenConverter();
        tokenConverter.setUserTokenConverter(ignoreAuthoritiesUserAuthenticationConverter);
        var converter = new JwtAccessTokenConverter();
        converter.setAccessTokenConverter(tokenConverter);
        converter.setSigningKey(jwtKey);
        return converter;
    }
}
