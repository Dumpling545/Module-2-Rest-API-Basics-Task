package com.epam.esm.propertiesholder;

import com.epam.esm.web.auth.authorizationserver.AuthoritiesToScopeTranslationTokenEnhancer;
import com.epam.esm.web.auth.authorizationserver.IgnoreAuthoritiesUserAuthenticationConverter;
import com.epam.esm.web.exceptionhandler.AuthorizationServerExceptionHandler;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Component;

import java.util.List;
@ConstructorBinding
@ConfigurationProperties(prefix = "oauth2.configuration")
public record OAuth2PropertiesHolder(
		String jwtKey,
		String secretKeyAlgorithm,
		String inMemoryClientName, String inMemoryClientSecret,
		List<String> inMemoryClientGrantTypes,
		String[] inMemoryClientRedirectUris) {
}
