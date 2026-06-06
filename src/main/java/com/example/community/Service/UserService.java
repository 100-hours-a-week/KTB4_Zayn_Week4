package com.example.community.Service;

import com.example.community.dto.JoinRequestDTO;
import com.example.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public int joinProcess(JoinRequestDTO joinRequestDTO) throws IllegalArgumentException {
        passwordCheck(joinRequestDTO.getUserPassword(), joinRequestDTO.getUserPasswordCheck());
        duplicatedCheck(joinRequestDTO.getUserEmail(), joinRequestDTO.getUserNickname());

        return userRepository.save(
                joinRequestDTO.getUserEmail(),
                passwordEncoding(joinRequestDTO.getUserPassword()),
                joinRequestDTO.getUserNickname(),
                joinRequestDTO.getUserImage()
        );
    }

    public Map<String, Object> getUserInfo() {
        int userId = getCurrentUserId();

        return Map.of(
                "user_id", userId,
                "user_email", userRepository.getUserEmailByUserId(userId),
                "user_nickname", userRepository.getUserNicknameByUserId(userId),
                "user_image", userRepository.getUserImageByUserId(userId)
        );
    }

    private void duplicatedCheck(String userEmail, String userNickname) {
        if (userRepository.existsByUserEmail(userEmail)) {
            throw new IllegalArgumentException("duplicated_user_email");
        }

        if (userRepository.existsByUserNickname(userNickname)) {
            throw new IllegalArgumentException("duplicated_user_nickname");
        }
    }

    private void passwordCheck(String userPassword, String userPasswordCheck) {
        if (!userPassword.equals(userPasswordCheck)) {
            throw new IllegalArgumentException("mismatch_user_password");
        }
    }

    private String passwordEncoding(String userPassword) {
        return passwordEncoder.encode(userPassword);
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