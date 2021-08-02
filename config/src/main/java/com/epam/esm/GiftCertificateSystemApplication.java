package com.epam.esm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Application starter class
 */
@SpringBootApplication
public class GiftCertificateSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(GiftCertificateSystemApplication.class, args);
    }
}
