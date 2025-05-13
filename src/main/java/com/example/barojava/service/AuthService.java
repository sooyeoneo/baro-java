package com.example.barojava.service;

import com.example.barojava.dto.AuthResponseDto;
import com.example.barojava.dto.LoginRequestDto;
import com.example.barojava.dto.SignUpRequestDto;

public interface AuthService {

    AuthResponseDto signUp(SignUpRequestDto signUpRequestDto);
    String login(LoginRequestDto loginRequestDto);
    AuthResponseDto grantAdminRole(Long userId, String requesterUsername);
}
