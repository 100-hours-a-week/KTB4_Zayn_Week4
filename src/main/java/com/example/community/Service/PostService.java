package com.example.community.Service;

import com.example.community.dto.CreatePostRequestDTO;
import com.example.community.repository.PostRepository;
import com.example.community.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final TokenProvider tokenProvider;

    public int createPostProcess(CreatePostRequestDTO createPostRequestDTO) {
        return postRepository.save(
                getCurrentUserId(),
                createPostRequestDTO.getPostTitle(),
                createPostRequestDTO.getPostContent(),
                createPostRequestDTO.getPostImage()
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
