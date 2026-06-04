package com.example.community.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/posts/{post_id}/comments")
public class CommentController { // 댓글 관련 요청 처리
    @PostMapping
    public ResponseEntity<Map<String, String>> writeComment(@PathVariable("post_id") Long postId) {
        return null;
    }

    @PatchMapping("/{comment_id}")
    public ResponseEntity<Map<String, String>> editComment(
            @PathVariable("post_id") Long postId, @PathVariable("comment_id") Long commentId) {
        return null;
    }

    @DeleteMapping("/{comment_id}")
    public ResponseEntity<Map<String, String>> deleteComment(
            @PathVariable("post_id") Long postId, @PathVariable("comment_id") Long commentId) {
        return null;
    }
}