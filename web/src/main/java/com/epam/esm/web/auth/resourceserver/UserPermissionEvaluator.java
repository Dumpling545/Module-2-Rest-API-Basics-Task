package com.epam.esm.web.auth.resourceserver;

import com.epam.esm.model.dto.OrderDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class UserPermissionEvaluator implements PermissionEvaluator {
    @Value("${oauth2.permissions.create}")
    private String createPermission;

    @Value("${oauth2.claims.user-id}")
    private String userIdClaim;

    @Value("SCOPE_${oauth2.scopes.orders.write-self}")
    private String writeSelfOrdersScope;
    @Value("SCOPE_${oauth2.scopes.orders.write-others}")
    private String writeOthersOrdersScope;

    private Integer extractUserId(Authentication authentication) {
        Jwt jwtToken = (Jwt) authentication.getPrincipal();
        return jwtToken.<Long>getClaim(userIdClaim).intValue();
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (targetDomainObject instanceof OrderDTO orderDTO) {
            return hasPermissionOnOrder(authentication, orderDTO, permission);
        } else {
            return false;
        }
    }

    private boolean hasPermissionOnOrder(Authentication authentication, OrderDTO orderDTO, Object permission) {
        if (createPermission.equals(permission)) {
            return hasCreatePermissionOnOrder(authentication, orderDTO);
        }
        return false;
    }

    private boolean hasCreatePermissionOnOrder(Authentication authentication, OrderDTO orderDTO) {
        Integer userId = extractUserId(authentication);
        if (orderDTO.getUserId() == null || orderDTO.getUserId().equals(userId)) {
            return authentication.getAuthorities().stream().anyMatch(ga -> ga.getAuthority().equals(writeSelfOrdersScope));
        } else {
            return authentication.getAuthorities().stream().anyMatch(ga -> ga.getAuthority().equals(writeOthersOrdersScope));
        }
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
                                 Object permission) {
        return false;
    }
}
