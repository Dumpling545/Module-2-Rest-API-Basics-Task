package com.epam.esm.web.auth.authorizationserver;

import com.epam.esm.model.dto.UserDTO;
import com.epam.esm.model.entity.User;
import com.epam.esm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Component
public class UserAuthenticationProvider implements AuthenticationProvider {

    //TODO add to properties
    private String passwordOrUserNameNotFound = "Password or user name not found";
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

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
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(userDTO.getRole().name());
        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(userDTO.getUserName(),
                userDTO.getPassword(), Collections.singleton(authority));
        result.setDetails(userDTO.getId());
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
