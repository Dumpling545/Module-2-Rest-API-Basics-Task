package com.epam.esm.db;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootConfiguration
@ComponentScan
@PropertySource({"classpath:db-configuration-test.properties"})
public class DatabaseTestConfig {
}
