package com.epam.esm.db.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

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
	public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
		return new NamedParameterJdbcTemplate(dataSource);
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
}


