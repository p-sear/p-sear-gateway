package com.pser.gateway.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@Table("user")
public class User extends BaseEntity {
    @Column("email")
    private String email;

    @Column("password")
    private String password;

    @Column("role")
    private RoleEnum role;

    @Builder
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}