package com.example.community.Service;

import com.example.community.dto.CommentRequestDTO;
import com.example.community.repository.CommentRepository;
import com.example.community.repository.PostRepository;
import com.example.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public int createCommentProcess(int postId, CommentRequestDTO commentRequestDTO) {
        int userId = getCurrentUserId();

        if (!postRepository.existsByPostId(postId))
            throw new IllegalArgumentException("post_not_found");

        int commentId = commentRepository.save(
                postId,
                userId,
                commentRequestDTO.getCommentContent()
        );

        postRepository.addCommentId(postId, commentId);
        userRepository.addUserCommentId(userId, commentId);

        return commentId;
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
