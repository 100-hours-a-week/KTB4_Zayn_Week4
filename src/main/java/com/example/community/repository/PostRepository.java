package com.example.community.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostRepository {
    private final Path path = Path.of("db/posts.json");
    private final ObjectMapper objectMapper;

    public int save(int userId, String title, String content, String postImage) {
        ObjectNode root = (ObjectNode) readUsersJson();
        ObjectNode posts = (ObjectNode) root.path("posts");

        int lastPostId = root.path("last_post_id").asInt();
        int newPostId = lastPostId + 1;

        root.put("last_post_id", newPostId);

        ObjectNode newPost = objectMapper.createObjectNode();
        newPost.put("post_id", newPostId);
        newPost.put("write_user_id", userId);
        newPost.put("title", title);
        newPost.put("content", content);
        newPost.put("post_image", postImage);
        newPost.put("created_at", getCurrentDateTime());
        newPost.putNull("updated_at");
        newPost.put("like_count", 0);
        newPost.put("comment_count", 0);
        newPost.putPOJO("comment_ids", List.of());

        posts.set(String.valueOf(newPostId), newPost);
        objectMapper.writeValue(path.toFile(), root);

        return newPostId;
    }

    private JsonNode readUsersJson() {
        return objectMapper.readTree(path.toFile());
    }

    private String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd: HH:mm:ss"));
    }
}