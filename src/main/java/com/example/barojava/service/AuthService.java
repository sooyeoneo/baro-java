package com.example.barojava.service;

import com.example.barojava.dto.AuthResponseDto;
import com.example.barojava.dto.LoginRequestDto;
import com.example.barojava.dto.SignUpRequestDto;

public interface AuthService {

    AuthResponseDto signUp(SignUpRequestDto requestDto);
    String login(LoginRequestDto requestDto);
    AuthResponseDto grantAdminRole(Long userId, String requesterUsername);
}
