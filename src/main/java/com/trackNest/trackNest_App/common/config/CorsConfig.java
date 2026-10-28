package com.trackNest.trackNest_App.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {

        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(
                    CorsRegistry registry) {

                registry.addMapping("/**")
                        .allowedOrigins(
                                "http://localhost:5173"
                        )
                        .allowedMethods("*")
                        .allowedHeaders("*")
                        // the JWT travels in an httpOnly cookie, so the browser must be allowed to send it
                        .allowCredentials(true);
            }
        };
    }
}