package com.example.barojava.service;

import com.example.barojava.dto.TokenResponseDto;
import com.example.barojava.exception.CustomException;
import com.example.barojava.jwt.JwtProvider;
import com.example.barojava.model.Role;
import com.example.barojava.model.User;
import com.example.barojava.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static com.example.barojava.exception.ErrorCode.INVALID_TOKEN;
import static com.example.barojava.exception.ErrorCode.USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReissueTest {

    private UserRepository userRepository;
    private JwtProvider jwtProvider;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        jwtProvider = mock(JwtProvider.class);
        authService = new AuthServiceImpl(userRepository, null, jwtProvider);
    }

    @Test
    @DisplayName("토큰 재발급 성공")
    void reissue_success() {
        String refreshToken = "valid.refresh.token";
        String username = "testuser";
        User user = User.builder()
                .id(1L)
                .username(username)
                .password("encodedPwd")
                .nickname("그로구")
                .roles(Set.of(Role.USER))
                .build();

        when(jwtProvider.validateToken(refreshToken)).thenReturn(true);
        when(jwtProvider.getUsername(refreshToken)).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(jwtProvider.createAccessToken(username, user.getRoles())).thenReturn("new.access.token");

        TokenResponseDto response = authService.reissue(refreshToken);

        assertEquals("new.access.token", response.getAccessToken());
        assertEquals(refreshToken, response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(7200, response.getExpiresIn());
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 유효하지 않은 Refresh Token")
    void reissue_fail_invalidToken() {
        String refreshToken = "invalid.token";

        when(jwtProvider.validateToken(refreshToken)).thenReturn(false);

        CustomException e = assertThrows(CustomException.class, () ->
                authService.reissue(refreshToken));

        assertEquals(INVALID_TOKEN, e.getErrorCode());
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 사용자 없음")
    void reissue_fail_userNotFound() {
        String refreshToken = "valid.token";
        String username = "ghost";

        when(jwtProvider.validateToken(refreshToken)).thenReturn(true);
        when(jwtProvider.getUsername(refreshToken)).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        CustomException e = assertThrows(CustomException.class, () ->
                authService.reissue(refreshToken));

        assertEquals(USER_NOT_FOUND, e.getErrorCode());
    }
}
