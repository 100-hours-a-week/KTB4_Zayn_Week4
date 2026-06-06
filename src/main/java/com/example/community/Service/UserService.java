package com.example.community.Service;

import com.example.community.dto.JoinRequestDTO;
import com.example.community.dto.UpdatePasswordDTO;
import com.example.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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

    public Map<String, Object> updateProfileProcess(String userNewNickname, String userNewImage) {
        int userId = getCurrentUserId();

        validateUserProfileChange(userId, userNewNickname, userNewImage);
        updateUserProfile(userId, userNewNickname, userNewImage);

        Map<String, Object> response = new HashMap<>();
        response.put("user_id", userId);
        response.put("user_nickname", userNewNickname);
        response.put("user_image", userNewImage);

        return response;
    }

    public void updatePasswordProcess(UpdatePasswordDTO updatePasswordDTO) {
        String userNewPassword = updatePasswordDTO.getUserNewPassword();
        String userNewPasswordCheck = updatePasswordDTO.getUserNewPassword();

        passwordCheck(userNewPassword, userNewPasswordCheck);
        updateUserPassword(getCurrentUserId(), passwordEncoding(userNewPassword));
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

    private void validateUserProfileChange(int userId, String userNewNickname, String userNewImage) {
        String beforeNickName = userRepository.getUserNicknameByUserId(userId);
        String beforeImage = userRepository.getUserImageByUserId(userId);

        if (userNewNickname.equals(beforeNickName) && userNewImage.equals(beforeImage)) {
            throw new IllegalArgumentException("no_user_update_changes");
        }
    }

    private void updateUserProfile(int userId, String userNewNickname, String userNewImage) {
        userRepository.setUserNicknameByUserId(userId, userNewNickname);
        userRepository.setUserImageByUserId(userId, userNewImage);
    }

    private void updateUserPassword(int userId, String encodedUserNewPassword) {
        userRepository.setUserPasswordByUserId(userId, encodedUserNewPassword);
    }
}