package com.pser.gateway.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pser.gateway.common.response.ApiResponse;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Hints;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ErrorWebExceptionHandler, ServerAccessDeniedHandler,
        ServerAuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    @NonNull
    public Mono<Void> handle(ServerWebExchange exchange, @NonNull Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ApiResponse<Void> errorResponse = ApiResponse.error(ex.getMessage());
        if (ex instanceof ResponseStatusException) {
            response.setStatusCode(((ResponseStatusException) ex).getStatusCode());
        } else if (ex instanceof AccessDeniedException) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            errorResponse.setMessage("권한이 필요한 API");
        } else if (ex instanceof AuthenticationException) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            errorResponse.setMessage("인증 없음");
        } else if (ex instanceof JwtException) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            errorResponse.setMessage(ex.getMessage());
        } else {
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            errorResponse.setMessage("알 수 없는 서버 에러");
        }

        return response.writeWith(new Jackson2JsonEncoder(objectMapper).encode(
                Mono.just(errorResponse),
                response.bufferFactory(),
                ResolvableType.forInstance(errorResponse),
                MediaType.APPLICATION_JSON,
                Hints.from(Hints.LOG_PREFIX_HINT, exchange.getLogPrefix())
        ));
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        return handle(exchange, (Throwable) denied);
    }


    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        return handle(exchange, ex);
    }
}