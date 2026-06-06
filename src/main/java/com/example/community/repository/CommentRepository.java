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

    private JsonNode readCommentsJson() {
        return objectMapper.readTree(path.toFile());
    }

    private String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd: HH:mm:ss"));
    }
}
