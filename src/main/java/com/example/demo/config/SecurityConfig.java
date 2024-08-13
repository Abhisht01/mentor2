package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/admin/**").hasRole("admin")
                                .requestMatchers("/mentor/**").hasRole("mentor")
                                .requestMatchers("/mentee/**").hasRole("mentee")
                                .anyRequest().authenticated()
                )
                .oauth2Login(oauth2Login ->
                        oauth2Login.userInfoEndpoint(userInfoEndpoint ->
                                userInfoEndpoint.oidcUserService(oidcUserService())
                        )
                );

        return http.build();
    }

    @Bean
    public OidcUserService oidcUserService() {
        OidcUserService delegate = new OidcUserService();
        return new OidcUserService() {
            @Override
            public OidcUser loadUser(OidcUserRequest userRequest) {
                OidcUser oidcUser = delegate.loadUser(userRequest);

                Set<SimpleGrantedAuthority> mappedAuthorities = oidcUser.getAuthorities().stream()
                        .map(authority -> new SimpleGrantedAuthority("ROLE_" + authority.getAuthority()))
                        .collect(Collectors.toSet());

                return new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
            }
        };
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri("http://localhost:8080/realms/your-realm/protocol/openid-connect/certs")
                .build();
    }
}

