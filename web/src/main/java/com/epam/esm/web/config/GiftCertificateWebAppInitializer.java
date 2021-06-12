package com.epam.esm.web.config;

import com.epam.esm.db.config.DBConfig;
import com.epam.esm.service.config.ServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.*;

public class GiftCertificateWebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[]{DBConfig.class, ServiceConfig.class};
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[]{WebConfig.class};
	}

	@Override
	protected String[] getServletMappings() {
		return new String[]{"/*"};
	}

	@Override
	protected Filter[] getServletFilters() {
		return new Filter[]{new CharacterEncodingFilter("UTF-8", true)};
	}

}
