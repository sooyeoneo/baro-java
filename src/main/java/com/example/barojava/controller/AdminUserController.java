package com.example.barojava.controller;

import com.example.barojava.dto.AuthResponseDto;
import com.example.barojava.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
@Tag(name = "Admin User API", description = "관리자 전용 사용자 권한 관리 API")
@SecurityRequirement(name = "JWT")
public class AdminUserController {

    private final AuthService authService;

    @Operation(summary = "관리자 권한 부여")
    @PatchMapping("/{userId}/role")
    public ResponseEntity<AuthResponseDto> grantAdminRole(
            @Parameter(description = "관리자 권한을 줄 사용자 ID") @PathVariable Long userId,
            @AuthenticationPrincipal String requesterUsername) {

        return ResponseEntity.ok(authService.grantAdminRole(userId, requesterUsername));
    }
}