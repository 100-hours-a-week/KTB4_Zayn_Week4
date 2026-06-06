package com.example.community.Service;

import com.example.community.dto.CreatePostRequestDTO;
import com.example.community.repository.PostRepository;
import com.example.community.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public int createPostProcess(CreatePostRequestDTO createPostRequestDTO) {
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
