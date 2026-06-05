package com.example.community.security;

public interface TokenProvider {
    public String createAccessToken(Long userId);
    public boolean validateAccessToken(String token);
}