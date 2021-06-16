package com.epam.esm.db;

import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@Configuration
@ComponentScan(basePackages = {"com.epam.esm.db"})
@PropertySource("classpath:sql.properties")
public class DatabaseTestConfig {
	@Profile("test")
	@Bean
	public DataSource embeddedDataSource() {
		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
				.addScript("classpath:postgresql-compatibility.sql").addScript("classpath:schema.sql")
				.addScript("classpath:test-data.sql").build();
	}
}
