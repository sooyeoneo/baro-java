package com.example.barojava.controller;

import com.example.barojava.dto.AuthResponseDto;
import com.example.barojava.dto.LoginRequestDto;
import com.example.barojava.dto.SignUpRequestDto;
import com.example.barojava.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signUp")
    public ResponseEntity<AuthResponseDto> signUp(@RequestBody SignUpRequestDto signUpRequestDto) {

        AuthResponseDto authResponseDto = authService.signUp(signUpRequestDto);

        return ResponseEntity.ok(authResponseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) {

        String token = authService.login(loginRequestDto);

        return ResponseEntity.ok().body(new TokenResponse(token));
    }

    @PatchMapping("/admin/users/{userId}/roles")
    public ResponseEntity<AuthResponseDto> grantAdminRole(
            @PathVariable Long userId,
            @AuthenticationPrincipal String requesterUsername) {

        AuthResponseDto authResponseDto = authService.grantAdminRole(userId, requesterUsername);

        return ResponseEntity.ok(authResponseDto);
    }

    private record TokenResponse(String token) {}
}
