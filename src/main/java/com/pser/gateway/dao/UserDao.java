package com.pser.gateway.dao;

import com.pser.gateway.domain.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface UserDao extends R2dbcRepository<User, Long> {
    Mono<User> findByEmail(String email);
}
