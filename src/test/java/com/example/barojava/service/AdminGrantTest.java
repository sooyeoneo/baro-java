package com.example.barojava.service;

import com.example.barojava.dto.AuthResponseDto;
import com.example.barojava.exception.CustomException;
import com.example.barojava.model.Role;
import com.example.barojava.model.User;
import com.example.barojava.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static com.example.barojava.exception.ErrorCode.ACCESS_DENIED;
import static com.example.barojava.exception.ErrorCode.USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AdminGrantTest {

    private UserRepository userRepository;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        authService = new AuthServiceImpl(userRepository, null, null); // JWT, 비밀번호 사용 안함
    }

    @Test
    @DisplayName("관리자 권한 부여 성공")
    void grantAdmin_success() {
        User adminUser = User.builder()
                .id(1L)
                .username("admin")
                .roles(Set.of(Role.ADMIN))
                .build();

        User targetUser = User.builder()
                .id(2L)
                .username("target")
                .nickname("요다")
                .roles(Set.of(Role.USER))
                .build();

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));

        AuthResponseDto response = authService.grantAdminRole(2L, "admin");

        assertEquals("target", response.getUsername());
        assertEquals("요다", response.getNickname());
        assertEquals(Set.of(Role.USER, Role.ADMIN), response.getRoles());
    }

    @Test
    @DisplayName("실패 - 요청자에 ADMIN 권한 없음")
    void grantAdmin_fail_notAdmin() {
        User requester = User.builder()
                .username("user")
                .roles(Set.of(Role.USER))
                .build();

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(requester));

        CustomException e = assertThrows(CustomException.class, () ->
                authService.grantAdminRole(2L, "user"));

        assertEquals(ACCESS_DENIED, e.getErrorCode());
    }

    @Test
    @DisplayName("실패 - 요청자 없음")
    void grantAdmin_fail_requesterNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        CustomException e = assertThrows(CustomException.class, () ->
                authService.grantAdminRole(2L, "ghost"));

        assertEquals(ACCESS_DENIED, e.getErrorCode());
    }

    @Test
    @DisplayName("실패 - 대상 사용자 없음")
    void grantAdmin_fail_targetNotFound() {
        User adminUser = User.builder()
                .username("admin")
                .roles(Set.of(Role.ADMIN))
                .build();

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        CustomException e = assertThrows(CustomException.class, () ->
                authService.grantAdminRole(999L, "admin"));

        assertEquals(USER_NOT_FOUND, e.getErrorCode());
    }
}