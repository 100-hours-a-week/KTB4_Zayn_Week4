package com.example.community.controller;

import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MainController { // 루트 경로 처리
    @GetMapping("/")
    public RequestEntity<Map<String, String>> index() {
        return null;
    }
}