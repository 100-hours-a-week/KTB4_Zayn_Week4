package com.example.community.controller;

import com.example.community.service.PostService;
import com.example.community.dto.PostRequestDTO;
import jakarta.validation.Valid;
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
    public ResponseEntity<Map<String, Object>> createPost(@Valid @RequestBody PostRequestDTO postRequestDTO) {
        int postId = postService.createPostProcess(postRequestDTO);

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
    public ResponseEntity<Map<String, Object>> updatePost(@PathVariable("post_id") int postId, @Valid @RequestBody PostRequestDTO postRequestDTO) {
        postService.updatePostProcess(postId, postRequestDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "post_edit_success");
        response.put("data", Map.of(
                "post_id", postId
        ));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{post_id}")
    public ResponseEntity<Map<String, Object>> deletePost(@PathVariable("post_id") int postId) {
        postService.deletePostProcess(postId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "post_delete_success");
        response.put("data", null);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{post_id}/edit")
    public ResponseEntity<Map<String, Object>> getPostEditForm(@PathVariable("post_id") int postId) {
        Map<String, Object> postInfo = postService.getPostInfo(postId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "post_edit_page_load");
        response.put("data", postInfo);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{post_id}/like")
    public ResponseEntity<Map<String, Object>> addLike(@PathVariable("post_id") int postId) {
        postService.addLikeProcess(postId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "like_update_success");
        response.put("data", Map.of(
                "liked", true,
                "like_count", 1
        ));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}