package com.example.community.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UserController { // 사용자 정보 관련 요청 처리
    @GetMapping("join")
    public ResponseEntity<Map<String, String>> getJoinForm() {
        return null;
    }

    @PostMapping("join")
    public ResponseEntity<Map<String, String>> tryJoin() {
        return null;
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