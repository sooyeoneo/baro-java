package com.example.barojava.service;

import com.example.barojava.dto.AuthResponseDto;
import com.example.barojava.dto.LoginRequestDto;
import com.example.barojava.dto.SignUpRequestDto;
import com.example.barojava.dto.TokenResponseDto;

public interface AuthService {

    AuthResponseDto signUp(SignUpRequestDto signUpRequestDto);
    TokenResponseDto login(LoginRequestDto loginRequestDto);
    TokenResponseDto reissue(String refreshToken);
    AuthResponseDto grantAdminRole(Long userId, String requesterUsername);
}