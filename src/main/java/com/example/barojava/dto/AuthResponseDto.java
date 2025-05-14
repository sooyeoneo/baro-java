package com.example.barojava.dto;

import com.example.barojava.model.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
public class AuthResponseDto {

    private String username;
    private String nickname;
    private Set<Role> roles;

    public AuthResponseDto(String username, String nickname, Set<Role> roles) {
        this.username = username;
        this.nickname = nickname;
        this.roles = roles;
    }
}
