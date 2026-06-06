package com.example.community.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MainController { // 루트 경로 처리
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> index() { // 필터 토큰 검사 성공하여야 해당 메서드 실행
        return ResponseEntity.ok(Map.of( // 하드 코딩, 추후 응답 객체 클래스 따로 만들어서 관리
                "message", "authorized",
                "data", Map.of(
                        "redirect_url", "/posts"
                )
        ));
    }
}