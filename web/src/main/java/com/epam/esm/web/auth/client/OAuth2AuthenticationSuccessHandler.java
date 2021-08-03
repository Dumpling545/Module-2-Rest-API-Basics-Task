package com.epam.esm.web.auth.client;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Setter
@Component
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "oauth2.client")
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	private String nameAttributeKey;
	private String idAttributeKey;
	@Value("${oauth2.claims.user-id}")
	private String userIdClaimName;
	@Value("${oauth2.claims.scopes}")
	private String scopesClaimName;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
	                                    Authentication authentication) throws IOException, ServletException {
		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
		HttpSession session = request.getSession(false);
		if (session != null) {
			var token = new UsernamePasswordAuthenticationToken(oAuth2User.getAttribute(nameAttributeKey),
			                                                    null, oAuth2User.getAuthorities());
			token.setDetails(oAuth2User.getAttribute(idAttributeKey));
			SecurityContextHolder.getContext().setAuthentication(token);
			SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
			response.sendRedirect(savedRequest.getRedirectUrl());
		}

	}
}
