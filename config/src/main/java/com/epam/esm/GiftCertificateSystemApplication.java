package com.epam.esm;

import com.epam.esm.propertiesholder.OAuth2PropertiesHolder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Application starter class
 */
@EnableJpaAuditing
@SpringBootApplication
@EnableJpaRepositories
@ConfigurationPropertiesScan
public class GiftCertificateSystemApplication {
	public static void main(String[] args) {
		SpringApplication.run(GiftCertificateSystemApplication.class, args);
	}
}
