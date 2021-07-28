package com.epam.esm;

import com.epam.esm.web.auth.authorizationserver.UserAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
public class OAuth2WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserAuthenticationProvider userAuthenticationProvider;
	@Value("${oauth2.resource-server.jwt.key-value}")
	private String jwtKey;
	@Value("${oauth2.resource-server.jwt.associated-secret-key-algorithm}")
	private String secretKeyAlgorithm;

	@Value("${oauth2.scopes.user}")
	private String userScope;
	@Value("${oauth2.scopes.admin}")
	private String adminScope;

	@Bean
	public JwtDecoder jwtDecoder() {
		byte [] key = jwtKey.getBytes();
		SecretKey originalKey = new SecretKeySpec(key, 0, key.length, secretKeyAlgorithm);
		return NimbusJwtDecoder.withSecretKey(originalKey).build();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.formLogin();
		http.authorizeRequests()
				.mvcMatchers("/").permitAll()
				.mvcMatchers("/signup").permitAll()
				.mvcMatchers("/login").permitAll()
				.mvcMatchers("/oauth/authorize").authenticated()
				.mvcMatchers(HttpMethod.GET, "/gift-certificates/**").permitAll()
				.mvcMatchers(HttpMethod.POST, "/orders/{id}").hasAnyAuthority(userScope, adminScope)
				.mvcMatchers(HttpMethod.GET, "/**").hasAnyAuthority(userScope, adminScope)
				.anyRequest().hasAuthority(adminScope);
		http.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
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
