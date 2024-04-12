package com.psear.gateway.dao;

import com.psear.gateway.domain.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface UserDao extends R2dbcRepository<User, Long> {
    Mono<User> findByEmail(String email);
}
