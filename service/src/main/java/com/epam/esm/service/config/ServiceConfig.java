package com.epam.esm.service.config;

import com.epam.esm.service.validator.GiftCertificateValidator;
import com.epam.esm.service.validator.TagValidator;
import com.epam.esm.service.validator.impl.GiftCertificateValidatorImpl;
import com.epam.esm.service.validator.impl.TagValidatorImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.ResourceBundle;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = {"com.epam.esm.service"})
public class ServiceConfig {

	private final static String VALIDATION_PROPERTIES =
			"validation";

	@Bean
	public TagValidator tagValidator() {
		ResourceBundle rb = ResourceBundle.getBundle(VALIDATION_PROPERTIES);
		TagValidatorImpl validator = new TagValidatorImpl(rb);
		return validator;
	}

	@Bean
	public GiftCertificateValidator giftCertificateValidator() {
		ResourceBundle rb = ResourceBundle.getBundle(VALIDATION_PROPERTIES);
		GiftCertificateValidatorImpl validator =
				new GiftCertificateValidatorImpl(rb);
		return validator;
	}
}
