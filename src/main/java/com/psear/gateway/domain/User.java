package com.psear.gateway.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@Table
public class User extends BaseEntity {
    @Column
    private String email;

    @Column
    private String password;

    @Column
    private RoleEnum role;

    @Builder
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}