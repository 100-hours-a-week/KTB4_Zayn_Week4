package com.example.community.controller;

import com.example.community.Service.CommentService;
import com.example.community.dto.CommentRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{post_id}/comments")
public class CommentController { // 댓글 관련 요청 처리
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> writeComment(@PathVariable("post_id") int postId, @Valid @RequestBody() CommentRequestDTO commentRequestDTO) {
        int commentId = commentService.createCommentProcess(postId, commentRequestDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "write_comment_success");
        response.put("data", commentId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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