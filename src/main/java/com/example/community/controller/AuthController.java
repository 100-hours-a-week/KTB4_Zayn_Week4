package com.example.community.controller;

import com.example.community.Service.AuthService;
import com.example.community.dto.LoginRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController { // 로그인, 로그아웃 관련 요청 처리
    private final AuthService authService;

    @GetMapping("/login")
    public ResponseEntity<Map<String, Object>> getLoginForm() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "login_page_load");
        response.put("data", null);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> tryLogin(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        Map<String, Object> userInfo = authService.loginProcess(loginRequestDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "login_success");
        response.put("data", userInfo);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> tryLogout() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "delete_token");
        response.put("data", null);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/token")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestHeader("Authorization") String authorization) {
        String accessToken = authService.refreshAccessToken(authorization);

        return ResponseEntity.ok(Map.of(
                "message", "access_token_refreshed",
                "data", accessToken
        ));
    }
}