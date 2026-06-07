package com.example.community.controller;

import com.example.community.common.ResponseFormat;
import com.example.community.service.AuthService;
import com.example.community.dto.LoginRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController { // 로그인, 로그아웃 관련 요청 처리
    private final AuthService authService;

    @GetMapping("/login")
    public ResponseEntity<?> getLoginForm() {
        return ResponseEntity.ok(ResponseFormat.of("login_page_load"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> tryLogin(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        Map<String, Object> userInfo = authService.loginProcess(loginRequestDTO);

        return ResponseEntity.ok(ResponseFormat.of("login_success", userInfo));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> tryLogout() {
        return ResponseEntity.ok(ResponseFormat.of("logout_success"));
    }

    @PostMapping("/token")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authorization) {
        String accessToken = authService.refreshAccessToken(authorization);

        return ResponseEntity.ok(
                ResponseFormat.of("access_token_refreshed",
                        Map.of("access_token", accessToken)
                )
        );
    }
}