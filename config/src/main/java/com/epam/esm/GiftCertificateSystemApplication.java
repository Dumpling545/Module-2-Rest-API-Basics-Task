package com.epam.esm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
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
