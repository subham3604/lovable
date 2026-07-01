package com.subham.projects.lovableClone.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Value("${client.url}")
    private String clientUrl;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                String wwwUrl = clientUrl.contains("://") && !clientUrl.contains("://www.")
                        ? clientUrl.replace("://", "://www.")
                        : clientUrl;

                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5173", clientUrl, wwwUrl)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
