package com.epam.esm;

import com.epam.esm.propertiesholder.OAuth2PropertiesHolder;
import com.epam.esm.web.ResourcePaths;
import com.epam.esm.web.auth.authorizationserver.UserAuthenticationProvider;
import com.epam.esm.web.auth.client.OAuth2AuthenticationSuccessHandler;
import com.epam.esm.web.auth.common.Scopes;
import com.epam.esm.web.auth.resourceserver.InvalidateSessionFilter;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.session.SessionManagementFilter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
@RequiredArgsConstructor
public class OAuth2WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

	private static final String ANY_GIFT_CERTIFICATES_PATH = ResourcePaths.GIFT_CERTIFICATES + "/**";
	private static final String ANY_TAGS_PATH = ResourcePaths.TAGS + "/**";
	private static final String ANY_ORDERS_PATH = ResourcePaths.ORDERS + "/**";
	private static final String ANY_USERS_PATH = ResourcePaths.USERS + "/**";
	private static final Logger logger = LoggerFactory.getLogger(OAuth2WebSecurityConfiguration.class);
	private final OAuth2PropertiesHolder propertiesHolder;
	@Autowired
	private UserAuthenticationProvider userAuthenticationProvider;

	@Bean
	public JwtDecoder jwtDecoder() {
		byte[] key = propertiesHolder.jwtKey().getBytes();
		SecretKey originalKey = new SecretKeySpec(key, 0, key.length, propertiesHolder.secretKeyAlgorithm());
		return NimbusJwtDecoder.withSecretKey(originalKey).build();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.formLogin();
		http.oauth2Login().successHandler(oAuth2AuthenticationSuccessHandler());
		http.csrf().disable();
		http.anonymous().authorities(Scopes.GUEST_SCOPES);
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
		http.addFilterBefore(new InvalidateSessionFilter(propertiesHolder.approvedPathPatternsForSession()),
		                     SessionManagementFilter.class);
		http.authorizeRequests()
				.mvcMatchers(ResourcePaths.ROOT).hasAuthority(Scopes.ROOT_READ)
				.mvcMatchers("/oauth/token").permitAll()
				.mvcMatchers("/oauth/authorize").permitAll()
				.mvcMatchers(HttpMethod.POST, ResourcePaths.USERS + "/register").hasAuthority(Scopes.USERS_WRITE_NEW)
				.mvcMatchers(HttpMethod.GET, ANY_GIFT_CERTIFICATES_PATH).hasAuthority(Scopes.GIFT_CERTIFICATES_READ)
				.mvcMatchers(HttpMethod.GET, ANY_TAGS_PATH).hasAuthority(Scopes.TAGS_READ)
				.mvcMatchers(HttpMethod.GET, ANY_USERS_PATH).hasAuthority(Scopes.USERS_READ)
				.mvcMatchers(HttpMethod.GET, ANY_ORDERS_PATH).hasAuthority(Scopes.ORDERS_READ)
				.mvcMatchers(ANY_GIFT_CERTIFICATES_PATH).hasAuthority(Scopes.GIFT_CERTIFICATES_WRITE)
				.mvcMatchers(ANY_ORDERS_PATH).hasAnyAuthority(Scopes.ORDERS_WRITE_SELF, Scopes.ORDERS_WRITE_OTHERS)
				.mvcMatchers(ANY_TAGS_PATH).hasAuthority(Scopes.TAGS_WRITE)
				.anyRequest().denyAll();
		http.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
	}

	@Bean
	public JWSSigner jwsSigner() throws KeyLengthException {
		return new MACSigner(propertiesHolder.jwtKey().getBytes(StandardCharsets.UTF_8));
	}

	@Bean
	public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() throws KeyLengthException {
		return new OAuth2AuthenticationSuccessHandler();
	}

	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
		return jwtAuthenticationConverter;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(userAuthenticationProvider);
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

}
