package com.example.community.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

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

        if (isAuthPageRequest(request)) {
            handleAuthPageRequest(authorization, request, response, filterChain);
            return;
        }

        if (isTokenRefreshRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!hasValidToken(authorization)) {
            setUnauthorizedResponse(response);
            return;
        }

        // 리팩토링 필수
        if (!authorization.startsWith("Bearer ")) {
            setUnauthorizedResponse(response);
            return;
        }

        String token = authorization.substring(7);

        if (!tokenProvider.validateAccessToken(token)) {
            setUnauthorizedResponse(response);
            return;
        }

        int userId = tokenProvider.getUserId(token);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        Collections.emptyList()
                );

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }

    private boolean isAuthPageRequest(HttpServletRequest request) {
        String method = request.getMethod();
        String path = request.getRequestURI();

        return ((method.equals("GET") || method.equals("POST"))
                && (path.equals("/login") || path.equals("/join")));
    }

    private void handleAuthPageRequest(String authorization, HttpServletRequest request,
                                       HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        if (hasValidToken(authorization)) { // 유효한 토큰이 있는 경우
            setAuthorizedResponse(response); // /posts로
            return;
        }

        filterChain.doFilter(request, response); // 유효 토큰이 없는 경우, 필터 종료
    }

    private boolean isTokenRefreshRequest(HttpServletRequest request) {
        return request.getMethod().equals("POST") && request.getRequestURI().equals("/token");
    }

    private boolean hasValidToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) { // 추후 문자열 상수 관리
            return false;
        }

        String token = authorization.substring(7);
        return tokenProvider.validateAccessToken(token);
    }

    private void setAuthorizedResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK); // 200
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("""
                {
                    "message" : "already_authorized",
                    "data" : {
                        "redirect_url" : "/posts"
                    }
                }
                """); // 하드코딩 추후 수정
    }

    private void setUnauthorizedResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("""
                {
                    "message" : "unauthorized",
                    "data" : {
                        "redirect_url" : "/login"
                    }
                }
                """); // 하드코딩 추후 수정
    }
}