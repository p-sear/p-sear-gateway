package com.example.gateway.dao;

import com.example.gateway.domain.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface UserDao extends R2dbcRepository<User, Long> {
    Mono<User> findByEmail(String email);
}
