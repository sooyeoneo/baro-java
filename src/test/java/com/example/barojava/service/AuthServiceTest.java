package com.example.barojava.service;

import com.example.barojava.dto.LoginRequestDto;
import com.example.barojava.dto.SignUpRequestDto;
import com.example.barojava.entity.Role;
import com.example.barojava.entity.User;
import com.example.barojava.exception.CustomException;
import com.example.barojava.jwt.JwtProvider;
import com.example.barojava.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static com.example.barojava.exception.ErrorCode.USER_ALREADY_EXISTS;
import static com.example.barojava.exception.ErrorCode.INVALID_CREDENTIALS;
import static com.example.barojava.exception.ErrorCode.ACCESS_DENIED;

class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtProvider jwtProvider;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtProvider = mock(JwtProvider.class);
        authService = new AuthServiceImpl(userRepository, passwordEncoder, jwtProvider);
    }

    @Test
    @DisplayName("회원가입 성공")
    void signUp_success() {
        SignUpRequestDto dto = new SignUpRequestDto("testuser", "1234", "멘토스");

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("1234")).thenReturn("encodedPwd");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = authService.signUp(dto);

        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getRoles()).contains(Role.USER);
    }

    @Test
    @DisplayName("회원가입 실패 - 이미 존재하는 사용자")
    void signUp_fail_userExists() {
        SignUpRequestDto dto = new SignUpRequestDto("testuser", "1234", "멘토스");

        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        CustomException e = assertThrows(CustomException.class, () -> authService.signUp(dto));
        assertEquals(USER_ALREADY_EXISTS, e.getErrorCode());
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        LoginRequestDto dto = new LoginRequestDto("testuser", "1234");
        User user = User.builder()
                .id(1L)
                .username("testuser")
                .password("encodedPwd")
                .nickname("멘토스")
                .roles(Set.of(Role.USER))
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("1234", "encodedPwd")).thenReturn(true);
        when(jwtProvider.createToken("testuser", user.getRoles())).thenReturn("jwt.token.here");

        String token = authService.login(dto);
        assertEquals("jwt.token.here", token);
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 틀림")
    void login_fail_wrongPassword() {
        LoginRequestDto dto = new LoginRequestDto("testuser", "wrong");
        User user = User.builder()
                .username("testuser")
                .password("encodedPwd")
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encodedPwd")).thenReturn(false);

        CustomException e = assertThrows(CustomException.class, () -> authService.login(dto));
        assertEquals(INVALID_CREDENTIALS, e.getErrorCode());
    }

    @Test
    @DisplayName("관리자 권한 부여 실패 - 요청자에 ADMIN 권한 없음")
    void grantAdmin_fail_notAdmin() {
        User normalUser = User.builder()
                .username("requester")
                .roles(Set.of(Role.USER))
                .build();

        when(userRepository.findByUsername("requester")).thenReturn(Optional.of(normalUser));

        CustomException e = assertThrows(CustomException.class, () ->
                authService.grantAdminRole(2L, "requester"));
        assertEquals(ACCESS_DENIED, e.getErrorCode());
    }
}
