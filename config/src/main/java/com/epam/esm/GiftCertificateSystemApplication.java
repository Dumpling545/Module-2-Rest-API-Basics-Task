package com.epam.esm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Application starter class
 */
@EnableJpaAuditing
@SpringBootApplication
@EnableJpaRepositories
@ConfigurationPropertiesScan
public class GiftCertificateSystemApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(GiftCertificateSystemApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(GiftCertificateSystemApplication.class, args);
	}
}
