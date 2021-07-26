package com.epam.esm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
public class OAuth2WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

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
		http.authorizeRequests()
				.mvcMatchers("/").permitAll()
				.mvcMatchers("/signup").permitAll()
				.mvcMatchers("/login").permitAll()
				.mvcMatchers(HttpMethod.GET, "/gift-certificates/**").permitAll()
				.mvcMatchers(HttpMethod.POST, "/orders/{id}").hasAnyAuthority(userScope, adminScope)
				.mvcMatchers(HttpMethod.GET, "/**").hasAnyAuthority(userScope, adminScope)
				.anyRequest().hasAuthority(adminScope);
		http.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
	}
	//TODO remove in-memory
	@Bean
	public UserDetailsService uds() {
		var uds = new InMemoryUserDetailsManager();

		var u = User.withUsername("john")
				.password("12345")
				.authorities("read")
				.build();

		uds.createUser(u);

		return uds;
	}
	//TODO remove in-memory
	@Bean
	public PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

}