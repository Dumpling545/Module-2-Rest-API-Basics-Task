package com.epam.esm.web.permissionevaluator;

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

	private Integer extractUserId(Authentication authentication){
		Jwt jwtToken = (Jwt) authentication.getPrincipal();
		String sub = jwtToken.getSubject();
		if(sub == null || sub.isEmpty()){
			return null;
		} else {
			return Integer.parseInt(sub);
		}
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
