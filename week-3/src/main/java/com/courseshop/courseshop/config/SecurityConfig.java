package com.courseshop.courseshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // The demo endpoints handle session/token checks in AuthController.
            .csrf(csrf -> csrf.ignoringRequestMatchers("/auth/**"))
            // Do not challenge Postman or browsers with Spring's default Basic login.
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize
                // Keep the existing project APIs open as they were before this demo.
                // /auth/session/me and /auth/token/me perform their own checks.
                .anyRequest().permitAll());

        return http.build();
    }
}
