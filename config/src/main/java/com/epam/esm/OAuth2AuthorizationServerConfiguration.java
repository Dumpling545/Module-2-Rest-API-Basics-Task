package com.epam.esm;

import com.epam.esm.web.auth.authorizationserver.AuthoritiesToScopeTranslationTokenEnhancer;
import com.epam.esm.web.auth.authorizationserver.IgnoreAuthoritiesUserAuthenticationConverter;
import com.epam.esm.web.auth.authorizationserver.InvalidateSessionImplicitTokenGranter;
import com.epam.esm.web.auth.common.Scopes;
import com.epam.esm.web.exceptionhandler.AuthorizationServerExceptionHandler;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.List;

/**
 * NOTE: Deprecated class is used since there is no current support for Implicit and Password owner flows at
 * Authorization Server side
 * NOTE: Single in-memory client used since there is no requirement for client management in the task
 */
@Configuration
@EnableAuthorizationServer
@ConfigurationProperties(prefix = "oauth2.auth-server")
public class OAuth2AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

	@Value("${oauth2.resource-server.jwt.key-value}")
	private String jwtKey;

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private AuthoritiesToScopeTranslationTokenEnhancer authoritiesToScopeTranslationTokenEnhancer;
	@Autowired
	private IgnoreAuthoritiesUserAuthenticationConverter ignoreAuthoritiesUserAuthenticationConverter;
	@Autowired
	private AuthorizationServerExceptionHandler authorizationServerExceptionHandler;

	@Setter
	private String inMemoryClientName;
	@Setter
	private String inMemoryClientSecret;
	@Setter
	private List<String> inMemoryClientGrantTypes;
	@Setter
	private String[] inMemoryClientRedirectUris;

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
				.withClient(inMemoryClientName)
				.secret(inMemoryClientSecret)
				.authorizedGrantTypes(inMemoryClientGrantTypes.toArray(String[]::new))
				.scopes(Scopes.ADMIN_SCOPES)
				.redirectUris(inMemoryClientRedirectUris);
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
		endpoints.authenticationManager(authenticationManager)
				.tokenStore(tokenStore())
				.tokenEnhancer(tokenEnhancerChain())
				.exceptionTranslator(authorizationServerExceptionHandler);
		var implicitTokenGranter = new InvalidateSessionImplicitTokenGranter(endpoints.getTokenServices(),
		                                                                     endpoints.getClientDetailsService(),
		                                                                     endpoints.getOAuth2RequestFactory());
		var passwordOwnerTokenGranter = new ResourceOwnerPasswordTokenGranter(authenticationManager,
		                                                                      endpoints.getTokenServices(),
		                                                                      endpoints.getClientDetailsService(),
		                                                                      endpoints.getOAuth2RequestFactory());
		var tokenGranter = new CompositeTokenGranter(List.of(implicitTokenGranter, passwordOwnerTokenGranter));
		endpoints.tokenGranter(tokenGranter);
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
