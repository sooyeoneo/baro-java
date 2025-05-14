package com.example.barojava.service;

import com.example.barojava.dto.AuthResponseDto;
import com.example.barojava.dto.LoginRequestDto;
import com.example.barojava.dto.SignUpRequestDto;
import com.example.barojava.dto.TokenResponseDto;
import com.example.barojava.exception.CustomException;
import com.example.barojava.jwt.JwtProvider;
import com.example.barojava.model.Role;
import com.example.barojava.model.User;
import com.example.barojava.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.example.barojava.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    public AuthResponseDto signUp(SignUpRequestDto request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException(USER_ALREADY_EXISTS);
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .roles(Set.of(Role.USER))
                .build();

        User savedUser = userRepository.save(user);
        return new AuthResponseDto(savedUser.getUsername(), savedUser.getNickname(), savedUser.getRoles());
    }

    @Override
    public TokenResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new CustomException(INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(INVALID_CREDENTIALS);
        }

        String accessToken = jwtProvider.createAccessToken(user.getUsername(), user.getRoles());
        String refreshToken = jwtProvider.createRefreshToken(user.getUsername());

        return new TokenResponseDto(accessToken, refreshToken, "Bearer", 7200);
    }

    @Override
    public TokenResponseDto reissue(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new CustomException(INVALID_TOKEN);
        }

        String username = jwtProvider.getUsername(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        String newAccessToken = jwtProvider.createAccessToken(user.getUsername(), user.getRoles());

        return new TokenResponseDto(newAccessToken, refreshToken, "Bearer", 7200);
    }

    @Override
    public AuthResponseDto grantAdminRole(Long userId, String requesterUsername) {
        User requester = userRepository.findByUsername(requesterUsername)
                .orElseThrow(() -> new CustomException(ACCESS_DENIED));

        if (!requester.getRoles().contains(Role.ADMIN)) {
            throw new CustomException(ACCESS_DENIED);
        }

        User target = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        target.addRole(Role.ADMIN);
        return new AuthResponseDto(target.getUsername(), target.getNickname(), target.getRoles());
    }
}