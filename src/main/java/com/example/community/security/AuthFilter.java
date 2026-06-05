package com.example.community.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws IOException, ServletException {
        String authorization = request.getHeader("Authorization");

        // JWT토큰이 없는 사용자
        if (authorization == null || !authorization.startsWith("Bearer ")) { // 추후 문자열 상수 관리
            setUnauthorizedResponse(response);
            return;
        }

        String token = authorization.substring(7); // 추후 매직넘버 관리

        // JWT토큰 유효하지 않음
        if (!tokenProvider.validateAccessToken(token)) {
            setUnauthorizedResponse(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void setUnauthorizedResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("""
                {
                    "message" : "unauthorized",
                    "data" : null
                }
                """); // 하드코딩 추후 수정
    }
}