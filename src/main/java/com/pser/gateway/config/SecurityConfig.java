package com.pser.gateway.config;

import com.pser.gateway.config.SecurityConfig.SecurityProperties;
import com.pser.gateway.dto.PathDto;
import com.pser.gateway.exception.GlobalExceptionHandler;
import com.pser.gateway.filter.JwtFilter;
import com.pser.gateway.filter.TrailingSlashRedirectFilter;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.FormLoginSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.HttpBasicSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.web.cors.reactive.CorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity
@Data
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain filterChain(
            ServerHttpSecurity builder,
            SecurityProperties securityProperties,
            TokenProvider tokenProvider,
            CorsConfigurationSource corsConfigurationSource,
            ErrorWebExceptionHandler globalExceptionHandler
    ) {
        builder.authorizeExchange(
                authorizeExchangeSpec -> authorizeExchangeSpec
                        .matchers(securityProperties.getAuthenticatedApiMatchers()).authenticated()
                        .matchers(securityProperties.getPermittedApiMatchers()).permitAll()
                        .anyExchange().authenticated()
        );
        builder.addFilterAt(new TrailingSlashRedirectFilter(), SecurityWebFiltersOrder.FIRST);
        builder.addFilterAt(new JwtFilter(tokenProvider), SecurityWebFiltersOrder.HTTP_BASIC);
        builder.exceptionHandling(exceptionHandlingSpec ->
                exceptionHandlingSpec
                        .accessDeniedHandler((GlobalExceptionHandler) globalExceptionHandler)
                        .authenticationEntryPoint((GlobalExceptionHandler) globalExceptionHandler));
        builder.formLogin(FormLoginSpec::disable);
        builder.httpBasic(HttpBasicSpec::disable);
        builder.csrf(CsrfSpec::disable);
        builder.cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource));
        return builder.build();
    }

    @ConfigurationProperties("spring.security.api")
    public static class SecurityProperties {
        private final List<PathDto> permittedApis;
        private final List<PathDto> authenticatedApis;

        public SecurityProperties(List<PathDto> permittedApis, List<PathDto> authenticatedApis) {
            this.permittedApis = permittedApis;
            this.authenticatedApis = authenticatedApis;
        }

        public PathPatternParserServerWebExchangeMatcher[] getPermittedApiMatchers() {
            return permittedApis.stream()
                    .map(PathDto::toMatcher)
                    .toList()
                    .toArray(new PathPatternParserServerWebExchangeMatcher[0]);
        }

        public PathPatternParserServerWebExchangeMatcher[] getAuthenticatedApiMatchers() {
            return authenticatedApis.stream()
                    .map(PathDto::toMatcher)
                    .toList()
                    .toArray(new PathPatternParserServerWebExchangeMatcher[0]);
        }
    }
}
