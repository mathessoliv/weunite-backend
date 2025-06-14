package com.example.weuniteauth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/verify-email/{email}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/send-reset-password").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/verify-reset-token/{email}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/reset-password/{username}").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/user/update/{username}").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/user/{username}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/user/username/{username}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/user/id/{id}").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(new JwtAuthenticationConverter())
                        )
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

}
