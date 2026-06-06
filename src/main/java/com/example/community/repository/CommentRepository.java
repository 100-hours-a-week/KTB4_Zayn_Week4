package com.example.community.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Repository
@RequiredArgsConstructor
public class CommentRepository {
    private final Path path = Path.of("db/comments.json");
    private final ObjectMapper objectMapper;

    public int save(int postId, int userId, String content) {
        ObjectNode root = (ObjectNode) readCommentsJson();
        ObjectNode comments = (ObjectNode) root.path("comments");

        int lastCommentId = root.path("last_comment_id").asInt();
        int newCommentId = lastCommentId + 1;

        root.put("last_comment_id", newCommentId);

        ObjectNode newComment = objectMapper.createObjectNode();
        newComment.put("comment_id", newCommentId);
        newComment.put("post_id", postId);
        newComment.put("write_user_id", userId);
        newComment.put("content", content);
        newComment.put("created_at", getCurrentDateTime());
        newComment.putNull("updated_at");

        comments.set(String.valueOf(newCommentId), newComment);

        objectMapper.writeValue(path.toFile(), root);

        return newCommentId;
    }

    public boolean existsByCommentId(int commentId) {
        return readCommentsJson()
                .path("comments")
                .has(String.valueOf(commentId));
    }

    public boolean isCommentInPost(int commentId, int postId) {
        return readCommentsJson()
                .path("comments")
                .path(String.valueOf(commentId))
                .path("post_id")
                .asInt() == postId;
    }

    public boolean isCommentWriter(int commentId, int userId) {
        return readCommentsJson()
                .path("comments")
                .path(String.valueOf(commentId))
                .path("write_user_id")
                .asInt() == userId;
    }

    public void updateComment(int commentId, String content) {
        ObjectNode root = (ObjectNode) readCommentsJson();

        ObjectNode comment = (ObjectNode) root
                .path("comments")
                .path(String.valueOf(commentId));

        comment.put("content", content);
        comment.put("updated_at", getCurrentDateTime());

        objectMapper.writeValue(path.toFile(), root);
    }

    private JsonNode readCommentsJson() {
        return objectMapper.readTree(path.toFile());
    }

    private String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd: HH:mm:ss"));
    }
}
