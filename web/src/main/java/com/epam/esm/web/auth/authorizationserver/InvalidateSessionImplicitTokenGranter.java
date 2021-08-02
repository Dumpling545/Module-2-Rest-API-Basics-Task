package com.epam.esm.web.auth.authorizationserver;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Extension of ImplicitTokenGranter which invalidates session after issuing JWT token
 */
public class InvalidateSessionImplicitTokenGranter extends ImplicitTokenGranter {
	public InvalidateSessionImplicitTokenGranter(
			AuthorizationServerTokenServices tokenServices,
			ClientDetailsService clientDetailsService,
			OAuth2RequestFactory requestFactory) {
		super(tokenServices, clientDetailsService, requestFactory);
	}

	protected InvalidateSessionImplicitTokenGranter(
			AuthorizationServerTokenServices tokenServices,
			ClientDetailsService clientDetailsService,
			OAuth2RequestFactory requestFactory, String grantType) {
		super(tokenServices, clientDetailsService, requestFactory, grantType);
	}

	@Override
	public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {
		OAuth2AccessToken token =  super.grant(grantType, tokenRequest);
		RequestAttributes requestAttributes = RequestContextHolder
				.currentRequestAttributes();
		ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
		HttpServletRequest request = attributes.getRequest();
		HttpSession session = request.getSession(false);
		if(session != null){
			session.invalidate();
		}
		return token;
	}

}
