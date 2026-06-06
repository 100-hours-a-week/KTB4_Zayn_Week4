package com.example.community.Service;

import com.example.community.dto.PostRequestDTO;
import com.example.community.repository.PostRepository;
import com.example.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public int createPostProcess(PostRequestDTO createPostRequestDTO) {
        return postRepository.save(
                getCurrentUserId(),
                createPostRequestDTO.getPostTitle(),
                createPostRequestDTO.getPostContent(),
                createPostRequestDTO.getPostImage()
        );
    }

    public Map<String, Object> postsPageLoadProcess(int page) {
        if (page < 1)
            throw new IllegalArgumentException("invalid_page");

        List<Map<String, Object>> allPosts = postRepository.findAllOrderByLatest();

        int pageSize = 10;
        int fromIdx = (page - 1) * pageSize;
        int toIdx = Math.min(fromIdx + pageSize, allPosts.size());

        List<Map<String, Object>> posts = fromIdx >= allPosts.size()
                ? List.of()
                : allPosts.subList(fromIdx, toIdx);

        return Map.of(
                "posts", posts,
                "page", page,
                "posts_count", posts.size()
        );
    }

    public Map<String, Object> getPostProcess(int postId) {
        if (!postRepository.existsByPostId(postId))
            throw new IllegalArgumentException("post_not_found");

        return postRepository.getPostByPostId(postId);
    }

    public void updatePostProcess(int postId, PostRequestDTO postRequestDTO) {
        int userId = getCurrentUserId();

        if (!postRepository.existsByPostId(postId))
            throw new IllegalArgumentException("post_not_found");

        if (!postRepository.isPostWriter(postId, userId))
            throw new IllegalArgumentException("post_update_forbidden");

        postRepository.updatePost(
                postId,
                postRequestDTO.getPostTitle(),
                postRequestDTO.getPostContent(),
                postRequestDTO.getPostImage()
        );
    }

    public void deletePostProcess(int postId) {
        int userId = getCurrentUserId();

        if (!postRepository.existsByPostId(postId))
            throw new IllegalArgumentException("post_not_found");

        if (!postRepository.isPostWriter(postId, userId))
            throw new IllegalArgumentException("post_update_forbidden");

        postRepository.deletePost(postId, userId);
        userRepository.removeUserPostId(userId, postId);
    }

    public Map<String, Object> getPostInfo(int postId) {
        int userId = getCurrentUserId();

        if (!postRepository.existsByPostId(postId))
            throw new IllegalArgumentException("post_not_found");

        if (!postRepository.isPostWriter(postId, userId))
            throw new IllegalArgumentException("post_update_forbidden");

        Map<String, Object> postInfo = new HashMap<>();
        postInfo.put("post_title", postRepository.getPostTitleByPostId(postId));
        postInfo.put("post_content", postRepository.getPostContentByPostId(postId));
        postInfo.put("post_image", postRepository.getPostImageByPostId(postId));

        return postInfo;
    }

    private int getCurrentUserId() {
        Object principal = Objects.requireNonNull(SecurityContextHolder
                        .getContext()
                        .getAuthentication())
                .getPrincipal();

        if (principal instanceof Integer userId)
            return userId;

        throw new IllegalArgumentException("invalid_authenticated_user");
    }
}
