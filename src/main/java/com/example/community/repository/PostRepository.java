package com.example.community.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class PostRepository {
    private final Path path = Path.of("db/posts.json");
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

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
        newPost.put("view_count", 0);
        newPost.put("comment_count", 0);
        newPost.putPOJO("comment_ids", List.of());

        posts.set(String.valueOf(newPostId), newPost);
        objectMapper.writeValue(path.toFile(), root);

        return newPostId;
    }

    public List<Map<String, Object>> findAllOrderByLatest() {
        JsonNode postsNode = readUsersJson().path("posts");

        List<Map<String, Object>> posts = new ArrayList<>();

        postsNode.properties().forEach(entry -> {
            JsonNode post = entry.getValue();
            int writeUserId = post.path("write_user_id").asInt();

            posts.add(Map.of(
                    "post_id", post.path("post_Id").asInt(),
                    "post_title", post.path("title").asString(),
                    "like_count", post.path("like_count").asString(),
                    "comment_count", post.path("comment_count").asInt(),
                    "view_count", post.path("view_count").asInt(),
                    "writer_nickname", userRepository.getUserNicknameByUserId(writeUserId),
                    "created_at", post.path("created_at").asString()
            ));
        });

        posts.sort((p1, p2) ->
                Integer.compare((int) p2.get("post_id"), (int) p1.get("post_id"))
        );

        return posts;
    }

    public boolean existsByPostId(int postId) {
        return readUsersJson()
                .path("posts")
                .has(String.valueOf(postId));
    }

    public Map<String, Object> getPostByPostId(int postId) {
        JsonNode post = readUsersJson()
                .path("posts")
                .path(String.valueOf(postId));

        int writeUserId = post.path("write_user_id").asInt();

        Map<String, Object> result = new HashMap<>();
        result.put("post_id", post.path("post_id").asInt());
        result.put("post_title", post.path("title").asString());
        result.put("post_content", post.path("content").asString());
        result.put("post_image", post.path("post_image").asString());
        result.put("like_count", post.path("like_count").asInt());
        result.put("comment_count", post.path("comment_count").asInt());
        result.put("view_count", post.path("view_count").asInt());
        result.put("writer_nickname", userRepository.getUserNicknameByUserId(writeUserId));
        result.put("comment_ids", objectMapper.convertValue(
                post.path("comment_ids"),
                List.class
        ));
        result.put("created_at", post.path("created_at").asString());
        result.put("updated_at", post.path("updated_at").isNull()
                ? null
                : post.path("updated_at").asString());

        return result;
    }

    private JsonNode readUsersJson() {
        return objectMapper.readTree(path.toFile());
    }

    private String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd: HH:mm:ss"));
    }
}