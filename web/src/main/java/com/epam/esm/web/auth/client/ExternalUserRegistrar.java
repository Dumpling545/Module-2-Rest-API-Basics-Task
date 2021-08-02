package com.epam.esm.web.auth.client;

import com.epam.esm.model.dto.UserDTO;
import com.epam.esm.service.UserService;
import com.epam.esm.web.auth.common.Scopes;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Setter
@Component
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "oauth2.client")
public class ExternalUserRegistrar extends DefaultOAuth2UserService {

	private final UserService userService;
	private Map<String, String> providerToIdKeyMap;
	private Map<String, String> providerToUserNameKeyMap;
	private String nameAttributeKey;
	private String idAttributeKey;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		String externalProvider = userRequest.getClientRegistration().getRegistrationId();
		String externalId = oAuth2User.getAttribute(providerToIdKeyMap.get(externalProvider)).toString();
		String userName = oAuth2User.getAttribute(providerToUserNameKeyMap.get(externalProvider)).toString();
		UserDTO userDTO = userService.getUser(externalId, externalProvider).orElseGet(() -> {
			UserDTO newUser = UserDTO.builder()
					.externalId(externalId)
					.externalProvider(externalProvider)
					.userName(userName)
					.build();
			return userService.registerUser(newUser);
		});
		Map<String, Object> attributes = Map.of(nameAttributeKey, userDTO.getUserName(),
		                                        idAttributeKey, userDTO.getId());
		String[] authorities = switch (userDTO.getRole()) {
			case USER -> Scopes.USER_SCOPES;
			case ADMIN -> Scopes.ADMIN_SCOPES;
		};
		List<SimpleGrantedAuthority> grantedAuthorities = Arrays.stream(authorities)
				.map(SimpleGrantedAuthority::new).toList();
		OAuth2User result = new DefaultOAuth2User(grantedAuthorities, attributes, nameAttributeKey);
		return result;
	}
}
