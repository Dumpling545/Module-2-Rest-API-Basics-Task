package com.epam.esm.web.config;


import com.epam.esm.web.exceptionhandler.ExceptionHelper;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.ResourceBundle;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.epam.esm.web")
@PropertySource("classpath:error-messages.properties")
public class WebConfig implements WebMvcConfigurer {
	@Bean
	public ObjectMapper objectMapper() {
		Jackson2ObjectMapperFactoryBean bean = new Jackson2ObjectMapperFactoryBean();
		bean.setIndentOutput(true);
		bean.setSimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		bean.setFeaturesToEnable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
		bean.afterPropertiesSet();
		ObjectMapper objectMapper = bean.getObject();
		objectMapper.registerModule(new JavaTimeModule());
		return objectMapper;
	}

	private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setObjectMapper(objectMapper());
		return converter;
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(mappingJackson2HttpMessageConverter());
	}
}
