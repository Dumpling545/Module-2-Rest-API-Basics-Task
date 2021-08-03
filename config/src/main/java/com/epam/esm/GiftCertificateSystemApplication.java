package com.epam.esm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Application starter class
 */
@EnableJpaAuditing
@SpringBootApplication
@EnableJpaRepositories
public class GiftCertificateSystemApplication {
	public static void main(String[] args) {
		SpringApplication.run(GiftCertificateSystemApplication.class, args);
	}
}
