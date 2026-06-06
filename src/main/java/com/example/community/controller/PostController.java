package com.example.community.controller;

import com.example.community.Service.PostService;
import com.example.community.dto.CreatePostRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController { // 게시글 관련 요청 처리
    private final PostService postService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getPosts(@RequestParam(defaultValue = "1") int page) {
        Map<String, Object> pageInfo = postService.postsPageLoadProcess(page);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "posts_page_load");
        response.put("data", pageInfo);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createPost(@RequestBody CreatePostRequestDTO createPostRequestDTO) {
        int postId = postService.createPostProcess(createPostRequestDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "posts_page_load");
        response.put("data", Map.of(
                "post_id", postId
        ));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/new")
    public ResponseEntity<Map<String, Object>> getNewPostForm() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "new_page_load");
        response.put("data", null);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{post_id}")
    public ResponseEntity<Map<String, Object>> getPost(@PathVariable("post_id") int postId) {
        Map<String, Object> post = postService.getPostProcess(postId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "post_details_page_load");
        response.put("data", post);

        return ResponseEntity.ok(response);
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