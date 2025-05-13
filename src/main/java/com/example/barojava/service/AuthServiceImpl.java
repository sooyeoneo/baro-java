package com.example.barojava.service;

import com.example.barojava.dto.AuthResponseDto;
import com.example.barojava.dto.LoginRequestDto;
import com.example.barojava.dto.SignUpRequestDto;
import com.example.barojava.entity.Role;
import com.example.barojava.entity.User;
import com.example.barojava.exception.CustomException;
import com.example.barojava.jwt.JwtProvider;
import com.example.barojava.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.example.barojava.exception.ErrorCode.ACCESS_DENIED;
import static com.example.barojava.exception.ErrorCode.INVALID_CREDENTIALS;
import static com.example.barojava.exception.ErrorCode.USER_ALREADY_EXISTS;
import static com.example.barojava.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    public AuthResponseDto signUp(SignUpRequestDto signUpRequestDto) {

        if (userRepository.existsByUsername(signUpRequestDto.getUsername())) {
            throw new CustomException(USER_ALREADY_EXISTS);
        }

        User user = User.builder()
                .username(signUpRequestDto.getUsername())
                .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
                .nickname(signUpRequestDto.getNickname())
                .roles(Set.of(Role.USER))
                .build();

        User savedUser =userRepository.save(user);

        return new AuthResponseDto(savedUser.getUsername(), savedUser.getNickname(), savedUser.getRoles());
    }

    @Override
    public String login(LoginRequestDto loginRequestDto) {

        User user = userRepository.findByUsername(loginRequestDto.getUsername())
                .orElseThrow(() -> new CustomException(INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new CustomException(INVALID_CREDENTIALS);
        }

        return jwtProvider.createToken(user.getUsername(), user.getRoles());
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
