package com.example.community.controller;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthController { // 로그인, 로그아웃 관련 요청 처리
    @GetMapping("/login")
    public ResponseEntity<Map<String, String>> getLoginForm() {
        return null;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> tryLogin() {
        return null;
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> tryLogout() {
        return null;
    }
}