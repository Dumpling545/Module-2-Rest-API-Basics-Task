package com.epam.esm.web.tokenenhancer;

import com.epam.esm.model.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserIdTokenEnhancer implements TokenEnhancer {
	//TODO remove logger
	private static final Logger logger = LoggerFactory.getLogger(UserIdTokenEnhancer.class);
	@Value("${oauth2.claims.user-id}")
	private String userIdClaim;
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		var token =
				new DefaultOAuth2AccessToken(accessToken);
		Map<String, Object> info =
				Map.of(userIdClaim, authentication.getUserAuthentication().getDetails());
		token.setAdditionalInformation(info);
		logger.info("Token enhancing: " + token.toString());
		return token;
	}
}
