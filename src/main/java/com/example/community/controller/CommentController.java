package com.example.community.controller;

import com.example.community.common.ResponseFormat;
import com.example.community.service.CommentService;
import com.example.community.dto.CommentRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{post_id}/comments")
public class CommentController { // 댓글 관련 요청 처리
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<?> writeComment(@PathVariable("post_id") int postId, @Valid @RequestBody() CommentRequestDTO commentRequestDTO) {
        int commentId = commentService.createCommentProcess(postId, commentRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseFormat.of("write_comment_success",
                        Map.of("comment_id", commentId)
                )
        );
    }

    @PatchMapping("/{comment_id}")
    public ResponseEntity<?> editComment(
            @PathVariable("post_id") int postId, @PathVariable("comment_id") int commentId,
            @Valid @RequestBody CommentRequestDTO commentRequestDTO) {
        commentService.editCommentProcess(postId, commentId, commentRequestDTO);
        return ResponseEntity.ok(
                ResponseFormat.of("comment_edit_success",
                        Map.of("comment_id", commentId)
                )
        );
    }

    @DeleteMapping("/{comment_id}")
    public ResponseEntity<?> deleteComment(
            @PathVariable("post_id") int postId, @PathVariable("comment_id") int commentId) {
        commentService.deleteCommentProcess(postId, commentId);
        return ResponseEntity.ok(ResponseFormat.of("comment_delete_success"));
    }
}