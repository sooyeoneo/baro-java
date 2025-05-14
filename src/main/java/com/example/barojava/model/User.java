package com.example.barojava.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class User {

    private final Long id;
    private final String username;
    private final String password;
    private final String nickname;

    private Role role = Role.USER;

    public void updateRole(Role role) {
        this.role = role;
    }
}

