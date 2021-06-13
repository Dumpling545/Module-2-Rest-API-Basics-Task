package com.epam.esm.db.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

/**
 * Spring configuration of db level
 */
@Configuration
@ComponentScan(basePackages = {"com.epam.esm.db"})
public class DBConfig {
	private static final String PRODUCTION_DB_PROPERTIES = "/hikari-production.properties";
	private static final String DEVELOPMENT_DB_PROPERTIES = "/hikari-development.properties";

	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Profile("production")
	@Bean
	public HikariDataSource dataSource() {
		HikariConfig config = new HikariConfig(PRODUCTION_DB_PROPERTIES);
		HikariDataSource dataSource = new HikariDataSource(config);
		return dataSource;
	}

	@Profile("development")
	@Bean
	public HikariDataSource developmentDataSource() {
		HikariConfig config = new HikariConfig(DEVELOPMENT_DB_PROPERTIES);
		HikariDataSource dataSource = new HikariDataSource(config);
		return dataSource;
	}

	@Profile("test")
	@Bean
	public DataSource embeddedDataSource() {
		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
				.addScript("classpath:postgresql-compatibility.sql").addScript("classpath:schema.sql")
				.addScript("classpath:test-data.sql").build();
	}
}


