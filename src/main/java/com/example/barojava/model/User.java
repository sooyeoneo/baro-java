package com.example.barojava.model;

import lombok.Builder;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class User {

    private final Long id;
    private final String username;
    private final String password;
    private final String nickname;
    private Set<Role> roles = new HashSet<>();

    @Builder
    public User(Long id, String username, String password, String nickname, Set<Role> roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nickname = nickname;

        // roles가 null이 아닌 경우에만 설정
        if (roles != null) {
            this.roles = new HashSet<>(roles);
        } else {
            // 기본값으로 USER 역할 추가
            this.roles.add(Role.USER);
        }
    }

    public Role getRole() {
        // 역할이 여러 개인 경우에도 하나의 역할만 반환해야 할 때
        // 관리자 역할을 우선적으로 반환
        if (roles.contains(Role.ADMIN)) {
            return Role.ADMIN;
        }
        return Role.USER;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void updateRole(Role role) {
        this.roles.clear();
        this.roles.add(role);
    }
}