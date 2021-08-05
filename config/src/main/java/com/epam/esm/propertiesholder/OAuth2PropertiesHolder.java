package com.epam.esm.propertiesholder;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.List;

@ConstructorBinding
@ConfigurationProperties(prefix = "oauth2.configuration")
public record OAuth2PropertiesHolder(
		String jwtKey,
		String secretKeyAlgorithm,
		String inMemoryClientName, String inMemoryClientSecret,
		List<String> inMemoryClientGrantTypes,
		String[] inMemoryClientRedirectUris,
		List<String> approvedPathPatternsForSession) {
}
