package com.example.community.controller;

import com.example.community.common.ResponseFormat;
import com.example.community.service.PostService;
import com.example.community.dto.PostRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController { // 게시글 관련 요청 처리
    private final PostService postService;

    @GetMapping
    public ResponseEntity<?> getPosts(@RequestParam(defaultValue = "1") int page) {
        Map<String, Object> pageInfo = postService.postsPageLoadProcess(page);

        return ResponseEntity.ok(ResponseFormat.of("posts_page_load", pageInfo));
    }

    @PostMapping
    public ResponseEntity<?> createPost(@Valid @RequestBody PostRequestDTO postRequestDTO) {
        int postId = postService.createPostProcess(postRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseFormat.of("post_page_load",
                        Map.of(
                                "post_id", postId
                        ))
        );
    }

    @GetMapping("/new")
    public ResponseEntity<?> getNewPostForm() {
        return ResponseEntity.ok(ResponseFormat.of("new_page_load"));
    }

    @GetMapping("/{post_id}")
    public ResponseEntity<?> getPost(@PathVariable("post_id") int postId) {
        Map<String, Object> post = postService.getPostProcess(postId);

        return ResponseEntity.ok(ResponseFormat.of("post_details_page_load", post));
    }

    @PatchMapping("/{post_id}")
    public ResponseEntity<?> updatePost(@PathVariable("post_id") int postId, @Valid @RequestBody PostRequestDTO postRequestDTO) {
        postService.updatePostProcess(postId, postRequestDTO);

        return ResponseEntity.ok(ResponseFormat.of("post_edit_success",
                Map.of(
                        "post_id", postId
                ))
        );
    }

    @DeleteMapping("/{post_id}")
    public ResponseEntity<?> deletePost(@PathVariable("post_id") int postId) {
        postService.deletePostProcess(postId);

        return ResponseEntity.ok(ResponseFormat.of("post_delete_success"));
    }

    @GetMapping("/{post_id}/edit")
    public ResponseEntity<?> getPostEditForm(@PathVariable("post_id") int postId) {
        Map<String, Object> postInfo = postService.getPostInfo(postId);
        return ResponseEntity.ok(ResponseFormat.of("post_edit_page_load", postInfo));
    }

    @PatchMapping("/{post_id}/like")
    public ResponseEntity<?> addLike(@PathVariable("post_id") int postId) {
        postService.addLikeProcess(postId);

        return ResponseEntity.ok(ResponseFormat.of("like_update_success",
                Map.of(
                        "liked", true,
                        "like_count", 1
                )));
    }
}