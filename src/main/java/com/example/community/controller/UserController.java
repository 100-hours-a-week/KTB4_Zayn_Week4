package com.example.community.controller;

import com.example.community.common.ResponseFormat;
import com.example.community.service.UserService;
import com.example.community.dto.JoinRequestDTO;
import com.example.community.dto.UpdatePasswordDTO;
import com.example.community.dto.UpdateProfileRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController { // 사용자 정보 관련 요청 처리
    private final UserService userService;

    @GetMapping("/join")
    public ResponseEntity<?> getJoinForm() {
        return ResponseEntity.ok(ResponseFormat.of("join_page_load"));
    }

    @PostMapping("/join")
    public ResponseEntity<?> tryJoin(@Valid @RequestBody JoinRequestDTO joinRequestDTO) {
        int userId = userService.joinProcess(joinRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseFormat.of("join_success",
                        Map.of(
                                "user_id", userId
                        ))
        );
    }

    @GetMapping("/me/profile")
    public ResponseEntity<?> getProfileEditForm() {
        Map<String, Object> userInfo = userService.getUserInfo();
        return ResponseEntity.ok(ResponseFormat.of("user_profile_edit_page_load", userInfo));
    }

    @PatchMapping("/me/profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody() UpdateProfileRequestDTO updateProfileRequestDTO) {
        Map<String, Object> userInfo = userService.updateProfileProcess(
                updateProfileRequestDTO.getUserNewNickname(),
                updateProfileRequestDTO.getUserNewImage()
        );

        return ResponseEntity.ok(ResponseFormat.of("profile_update_success", userInfo));
    }

    @GetMapping("/me/password")
    public ResponseEntity<?> getPasswordEditForm() {
        return ResponseEntity.ok(ResponseFormat.of("user_password_edit_page_load"));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody() UpdatePasswordDTO updatePasswordDTO) {
        userService.updatePasswordProcess(updatePasswordDTO);
        return ResponseEntity.ok(ResponseFormat.of("password_update_success"));
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<?> tryWithdraw() {
        userService.withdrawProcess();
        return ResponseEntity.ok(ResponseFormat.of("user_delete_success"));
    }
}