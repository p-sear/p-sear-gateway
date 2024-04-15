package com.pser.gateway.dto;

import com.pser.gateway.domain.RoleEnum;
import java.util.Collection;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Getter
public class UserDetailsImpl extends User {
    private final Long id;
    private final RoleEnum role;

    public UserDetailsImpl(Long id, String username, String password,
                           Collection<? extends GrantedAuthority> authorities, RoleEnum role) {
        super(username, password, authorities);
        this.id = id;
        this.role = role;
    }
}
