package com.example.community.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.nio.file.Path;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final Path path = Path.of("db/users.json");
    private final ObjectMapper objectMapper;

    public boolean existsByUserEmail(String userEmail) {
        return readUsersJson()
                .path("user_emails")
                .has(userEmail);
    }

    public boolean existsByUserNickname(String userNickName) {
        return readUsersJson()
                .path("user_nicknames")
                .has(userNickName);
    }

    // 메서드 역할은 DB에 저장하는 기능인데, 너무 많은 책임을 가짐 -> 추후 리팩토링
    public int save(String newUserEmail, String newUserPassword, String newUserNickname, String newUserImage) {
        ObjectNode root = (ObjectNode) readUsersJson();
        ObjectNode userEmails = (ObjectNode) root.path("user_emails");
        ObjectNode userNicknames = (ObjectNode) root.path("user_nicknames");
        ObjectNode users = (ObjectNode) root.path("users");

        int lastUserId = root.path("last_user_id").asInt();
        int newUserId = lastUserId + 1;

        root.put("last_user_id", lastUserId + 1);
        userEmails.put(newUserEmail, newUserId);
        userNicknames.put(newUserNickname, newUserId);

        ObjectNode newUser = objectMapper.createObjectNode();
        newUser.put("user_id", newUserId);
        newUser.put("user_email", newUserEmail);
        newUser.put("user_password", newUserPassword);
        newUser.put("user_nickname", newUserNickname);
        newUser.put("user_image", newUserImage);

        ObjectNode userPosts = objectMapper.createObjectNode();
        userPosts.put("count", 0);
        userPosts.putPOJO("post_ids", List.of());

        ObjectNode userComments = objectMapper.createObjectNode();
        userComments.put("count", 0);
        userComments.putPOJO("comment_ids", List.of());

        newUser.set("user_posts", userPosts);
        newUser.set("user_comments", userComments);

        users.set(String.valueOf(newUserId), newUser);

        objectMapper.writeValue(path.toFile(), root);

        return newUserId;
    }

    public String getUserNicknameByUserId(int userId) {
        return readUsersJson()
                .path("users")
                .path(String.valueOf(userId))
                .path("user_nickname")
                .asString();
    }

    public String getUserEmailByUserId(int userId) {
        return readUsersJson()
                .path("users")
                .path(String.valueOf(userId))
                .path("user_email")
                .asString();
    }

    public String getUserImageByUserId(int userId) {
        return readUsersJson()
                .path("users")
                .path(String.valueOf(userId))
                .path("user_image")
                .asString();
    }

    public String getUserPasswordByUserId(int userId) {
        return readUsersJson()
                .path("users")
                .path(String.valueOf(userId))
                .path("user_password")
                .asString();
    }

    public int getUserIdByUserEmail(String userEmail) {
        return readUsersJson()
                .path("user_emails")
                .path(userEmail)
                .asInt();
    }

    private JsonNode readUsersJson() {
        return objectMapper.readTree(path.toFile());
    }
}