package com.example.community.service;

import com.example.community.dto.LoginRequestDTO;
import com.example.community.repository.UserRepository;
import com.example.community.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public Map<String, Object> loginProcess(LoginRequestDTO loginRequestDTO) {
        // 1. db 조회 사용자 ID, 비밀번호 확인
        int userId = authenticateUser(loginRequestDTO.getUserEmail(), loginRequestDTO.getUserPassword());

        // 2. 액세스 토큰 및 리프래시 토큰 생성
        String accessToken = tokenProvider.createAccessToken(userId);
        String refreshToken = tokenProvider.createRefreshToken(userId);

        String userNickname = userRepository.getUserNicknameByUserId(userId);
        String userEmail = userRepository.getUserEmailByUserId(userId);
        String userImage = userRepository.getUserImageByUserId(userId);

        // 3. 토큰 + 사용자정보(유저 닉네임, 이메일, 이미지, 아이디) 담은 응답 반환
        return Map.of(
                "user_nickname", userNickname,
                "user_email", userEmail,
                "user_image", userImage,
                "access_token", accessToken,
                "refresh_token", refreshToken
        );
    }

    public String refreshAccessToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new IllegalArgumentException("invalid_refresh_token");
        }

        String refreshToken = authorization.substring(7);


        if (!tokenProvider.validateRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("invalid_refresh_token");
        }

        int userId = tokenProvider.getUserId(refreshToken);

        return tokenProvider.createAccessToken(userId);
    }

    private int authenticateUser(String userEmail, String userPassword) {
        if (!userRepository.existsByUserEmail(userEmail)) {
            throw new IllegalArgumentException("user_email_not_found");
        }

        // 비밀번호 일치 여부
        int userId = userRepository.getUserIdByUserEmail(userEmail);
        String encodedPassword = userRepository.getUserPasswordByUserId(userId);

        if (!passwordEncoder.matches(userPassword, encodedPassword)) {
            throw new IllegalArgumentException("password_failed");
        }

        return userId;
    }
}