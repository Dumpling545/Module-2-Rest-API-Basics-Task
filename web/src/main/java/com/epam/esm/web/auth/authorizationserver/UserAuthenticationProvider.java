package com.epam.esm.web.auth.authorizationserver;

import com.epam.esm.model.dto.UserDTO;
import com.epam.esm.model.entity.User;
import com.epam.esm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class UserAuthenticationProvider implements AuthenticationProvider {

    @Value("${auth.exception.password-username-not-found}")
    private String passwordOrUserNameNotFound;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${oauth2.roles.admin}")
    private String[] adminScopes;

    @Value("${oauth2.roles.user}")
    private String[] userScopes;

    @Value("${oauth2.roles.guest}")
    private String[] guestScopes;

    private UnauthorizedUserException createPasswordOrUserNameNotFoundException(){
        return new UnauthorizedUserException(passwordOrUserNameNotFound);
    }
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        Optional<UserDTO> userDtoOptional = userService.getUser(token.getName());
        UserDTO userDTO =  userDtoOptional.orElseThrow(this::createPasswordOrUserNameNotFoundException);
        if(!passwordEncoder.matches(authentication.getCredentials().toString(), userDTO.getPassword())){
            throw createPasswordOrUserNameNotFoundException();
        }
        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(userDTO.getUserName(),
                userDTO.getPassword(), getScopes(userDTO));
        result.setDetails(userDTO.getId());
        return result;
    }

    private Set<? extends GrantedAuthority> getScopes(UserDTO userDTO){
        String[] scopes = switch (userDTO.getRole()){
            case USER -> userScopes;
            case ADMIN -> adminScopes;
            default -> guestScopes;
        };
        return Arrays.stream(scopes).map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
