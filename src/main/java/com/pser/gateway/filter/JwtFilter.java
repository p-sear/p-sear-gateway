package com.pser.gateway.filter;

import com.pser.gateway.Util;
import com.pser.gateway.config.TokenProvider;
import com.pser.gateway.dto.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@RequiredArgsConstructor
public class JwtFilter implements WebFilter {
    private final TokenProvider tokenProvider;

    @Override
    @NonNull
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String token = resolveToken(exchange);
        if (!Util.isNullOrBlank(token)) {
            return tokenProvider.getAuthentication(token)
                    .flatMap(authentication -> {
                        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                        addAuthHeader(exchange, userDetails);
                        Context context = ReactiveSecurityContextHolder.withAuthentication(authentication);
                        return chain.filter(exchange).contextWrite(context);
                    });
        }
        return chain.filter(exchange);
    }

    private String resolveToken(ServerWebExchange exchange) {
        String token = null;
        try {
            HttpHeaders headers = exchange.getRequest().getHeaders();
            String bearerToken = headers.getFirst(HttpHeaders.AUTHORIZATION);
            if (bearerToken != null) {
                token = bearerToken.substring("Bearer".length() + 1);
            }
            return token;
        } catch (Exception e) {
            return token;
        }
    }

    private void addAuthHeader(ServerWebExchange exchange, UserDetailsImpl userDetails) {
        exchange.getRequest().mutate().headers(
                httpHeaders -> {
                    httpHeaders.add("User-Id", userDetails.getId().toString());
                    httpHeaders.add("User-Email", userDetails.getUsername());
                    httpHeaders.add("User-Role", userDetails.getRole().name());
                }
        );
    }
}
