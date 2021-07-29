package com.epam.esm;

import com.epam.esm.web.ResourcePaths;
import com.epam.esm.web.auth.authorizationserver.UserAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.stream.Stream;

@Configuration
public class OAuth2WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String ANY_GIFT_CERTIFICATES_PATH = ResourcePaths.GIFT_CERTIFICATES + "/**";
    private static final String ANY_TAGS_PATH = ResourcePaths.TAGS + "/**";
    private static final String ANY_ORDERS_PATH = ResourcePaths.ORDERS + "/**";
    private static final String ANY_USERS_PATH = ResourcePaths.USERS + "/**";
    @Autowired
    private UserAuthenticationProvider userAuthenticationProvider;
    @Value("${oauth2.resource-server.jwt.key-value}")
    private String jwtKey;
    @Value("${oauth2.resource-server.jwt.associated-secret-key-algorithm}")
    private String secretKeyAlgorithm;
    @Value("${oauth2.roles.guest}")
    private String[] guestScopesWithoutPrefix;
    @Value("SCOPE_${oauth2.scopes.root.read}")
    private String readRootScope;
    @Value("SCOPE_${oauth2.scopes.root.authorize-endpoint}")
    private String authorizeEndpointRootScope;
    @Value("SCOPE_${oauth2.scopes.root.token-endpoint}")
    private String tokenEndpointRootScope;
    @Value("SCOPE_${oauth2.scopes.gift-certificates.read}")
    private String readGiftCertificatesScope;
    @Value("SCOPE_${oauth2.scopes.gift-certificates.write}")
    private String writeGiftCertificatesScope;
    @Value("SCOPE_${oauth2.scopes.tags.read}")
    private String readTagsScope;
    @Value("SCOPE_${oauth2.scopes.tags.write}")
    private String writeTagsScope;
    @Value("SCOPE_${oauth2.scopes.users.read}")
    private String readUsersScope;
    @Value("SCOPE_${oauth2.scopes.users.write-new}")
    private String writeNewUserScope;
    @Value("SCOPE_${oauth2.scopes.orders.read}")
    private String readOrdersScope;
    @Value("SCOPE_${oauth2.scopes.orders.write-self}")
    private String writeSelfOrdersScope;
    @Value("SCOPE_${oauth2.scopes.orders.write-others}")
    private String writeOthersOrdersScope;

    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] key = jwtKey.getBytes();
        SecretKey originalKey = new SecretKeySpec(key, 0, key.length, secretKeyAlgorithm);
        return NimbusJwtDecoder.withSecretKey(originalKey).build();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String[] guestScopes = Stream.of(guestScopesWithoutPrefix)
                .map(s -> String.format("SCOPE_%s", s)).toArray(String[]::new);
        http.formLogin();
        http.csrf().disable();
        http.anonymous().authorities(guestScopes);
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
        http.authorizeRequests()
                .mvcMatchers(ResourcePaths.ROOT).hasAuthority(readRootScope)
                .mvcMatchers("oauth/token").hasAuthority(tokenEndpointRootScope)
                .mvcMatchers("oauth/authorize").not().anonymous()
                .mvcMatchers(HttpMethod.POST, ResourcePaths.USERS + "/register").hasAuthority(writeNewUserScope)
                .mvcMatchers(HttpMethod.GET, ANY_GIFT_CERTIFICATES_PATH).hasAuthority(readGiftCertificatesScope)
                .mvcMatchers(HttpMethod.GET, ANY_TAGS_PATH).hasAuthority(readTagsScope)
                .mvcMatchers(HttpMethod.GET, ANY_USERS_PATH).hasAuthority(readUsersScope)
                .mvcMatchers(HttpMethod.GET, ANY_ORDERS_PATH).hasAuthority(readOrdersScope)
                .mvcMatchers(ANY_GIFT_CERTIFICATES_PATH).hasAuthority(writeGiftCertificatesScope)
                .mvcMatchers(ANY_ORDERS_PATH).hasAnyAuthority(writeSelfOrdersScope, writeOthersOrdersScope)
                .anyRequest().denyAll();
        http.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(userAuthenticationProvider);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
