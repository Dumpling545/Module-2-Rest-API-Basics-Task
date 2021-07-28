package com.epam.esm.web.auth.resourceserver;

import com.epam.esm.model.dto.OrderDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class UserPermissionEvaluator implements PermissionEvaluator {
	@Value("${oauth2.scopes.admin}")
	private String adminScope;

	@Value("${oauth2.permissions.create}")
	private String createPermission;

	@Value("${oauth2.claims.user-id}")
	private String userIdClaim;

	private Integer extractUserId(Authentication authentication){
		Jwt jwtToken = (Jwt) authentication.getPrincipal();
		return jwtToken.getClaim(userIdClaim);
	}
	private boolean isAdmin(Authentication authentication){
		return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(adminScope));
	}
	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		if(targetDomainObject instanceof OrderDTO orderDTO){
			return hasPermissionOnOrder(authentication, orderDTO, permission);
		} else {
			return false;
		}
	}

	private boolean hasPermissionOnOrder(Authentication authentication, OrderDTO orderDTO, Object permission) {
		Integer userId = extractUserId(authentication);
		boolean isOrderWithoutUserId = orderDTO.getUserId() == null;
		if (createPermission.equals(permission)) {
			return isAdmin(authentication) || isOrderWithoutUserId || orderDTO.getUserId().equals(userId);
		}
		return false;
	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
	                             Object permission) {
		return false;
	}
}
