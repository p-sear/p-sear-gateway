package com.pser.gateway.config;

import com.pser.gateway.config.CorsConfig.CorsProperties;
import java.util.List;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableConfigurationProperties(CorsProperties.class)
public class CorsConfig {

    @Bean("corsConfigurationSource")
    CorsConfigurationSource corsConfigurationSource(CorsProperties corsProperties) {
        List<String> allowedMethods = List.of("*");
        List<String> allowedHeaders = List.of("*");

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(corsProperties.getOrigins());
        configuration.setAllowedMethods(allowedMethods);
        configuration.setAllowedHeaders(allowedHeaders);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Getter
    @ConfigurationProperties("spring.security.cors")
    public static class CorsProperties {
        private final List<String> origins;

        public CorsProperties(List<String> origins) {
            this.origins = origins;
        }
    }
}