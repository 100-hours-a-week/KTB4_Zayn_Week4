package com.example.community.controller;

import com.example.community.Service.UserService;
import com.example.community.dto.JoinRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController { // 사용자 정보 관련 요청 처리
    private final UserService userService;

    @GetMapping("/join")
    public ResponseEntity<Map<String, Object>> getJoinForm() {
        Map<String, Object> response = new HashMap<>(); // 추후 응답 관련 클래스 따로 만들기
        response.put("message", "join_page_load");
        response.put("data", null);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/join")
    public ResponseEntity<Map<String, Object>> tryJoin(@Valid @RequestBody JoinRequestDTO joinRequestDTO) {
        Integer userId = userService.joinProcess(joinRequestDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "join_success");
        response.put("data", Map.of(
                "user_id", userId
        ));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me/profile")
    public ResponseEntity<Map<String, String>> getProfileEditForm() {
        return null;
    }

    @PatchMapping("/me/profile")
    public ResponseEntity<Map<String, String>> updateProfile() {
        return null;
    }

    @GetMapping("/me/password")
    public ResponseEntity<Map<String, String>> getPasswordEditForm() {
        return null;
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Map<String, String>> updatePassword() {
        return null;
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<Map<String, String>> tryWithdraw() {
        return null;
    }
}