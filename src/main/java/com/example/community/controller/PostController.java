package com.example.community.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/posts")
public class PostController { // 게시글 관련 요청 처리
    @GetMapping
    public ResponseEntity<Map<String, String>> getPosts(@RequestParam Long page) {
        return null;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createPost() {
        return null;
    }

    @GetMapping("/new")
    public ResponseEntity<Map<String, String>> getNewPostForm() {
        return null;
    }

    @GetMapping("/{post_id}")
    public ResponseEntity<Map<String, String>> getPost(@PathVariable("post_id") Long postId) {
        return null;
    }

    @PatchMapping("/{post_id}")
    public ResponseEntity<Map<String, String>> updatePost(@PathVariable("post_id") Long postId) {
        return null;
    }

    @DeleteMapping("/{post_id}")
    public ResponseEntity<Map<String, String>> deletePost(@PathVariable("post_id") Long postId) {
        return null;
    }

    @GetMapping("/{post_id}/edit")
    public ResponseEntity<Map<String, String>> getPostEditForm(@PathVariable("post_id") Long postId) {
        return null;
    }

    @PatchMapping("/{post_id}/like")
    public ResponseEntity<Map<String, String>> addLike(@PathVariable("post_id") Long postId) {
        return null;
    }
}