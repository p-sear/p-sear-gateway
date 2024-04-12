package com.example.gateway.application;

import com.example.gateway.dao.UserDao;
import com.example.gateway.domain.RoleEnum;
import com.example.gateway.dto.UserDetailsImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {
    private final UserDao userDao;

    public Mono<UserDetails> findByUsername(String username) throws UsernameNotFoundException {
        return userDao.findByEmail(username)
                .switchIfEmpty(Mono.error(new NotFoundException("유저를 찾을 수 없습니다")))
                .map(user ->
                        new UserDetailsImpl(
                                user.getId(),
                                user.getEmail(),
                                user.getPassword(),
                                toAuthorities(user.getRole())
                        )
                );
    }

    private List<SimpleGrantedAuthority> toAuthorities(RoleEnum role) {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
}
