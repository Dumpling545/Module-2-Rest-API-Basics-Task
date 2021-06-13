package com.epam.esm.service.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = {"com.epam.esm.service"})
@PropertySource("classpath:validation.properties")
@PropertySource("classpath:exception.properties")
public class ServiceConfig {}
