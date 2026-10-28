package com.trackNest.trackNest_App.common.config;

import com.trackNest.trackNest_App.common.security.JWTAuthenticationFilter;
import com.trackNest.trackNest_App.common.security.JWTTokenGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {

    private final JWTAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JWTAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                // lets the CORS preflight (OPTIONS) through before the auth check; without it the browser blocks
                // every JSON/multipart call to a protected endpoint
                .cors(Customizer.withDefaults())
                // not logged in / cookie expired: answer 401 (the frontend redirects to login) instead of 403
                .exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .csrf(
                        csrf->csrf.disable()
                )
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers("/auth/register", "/auth/login","/auth/csrf").permitAll()
                                        .anyRequest().authenticated()
                ).addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class

                ).build();

    }

}