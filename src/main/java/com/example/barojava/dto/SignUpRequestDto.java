package com.example.barojava.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpRequestDto {

    private String username;
    private String password;
    private String nickname;
}
