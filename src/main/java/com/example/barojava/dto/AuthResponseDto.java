package com.example.barojava.dto;

import com.example.barojava.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class AuthResponseDto {

    private String username;
    private String nickname;
    private Set<Role> roles;
}
