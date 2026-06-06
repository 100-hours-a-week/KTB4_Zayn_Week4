package com.example.community.security;

public interface TokenProvider {
    public String createAccessToken(int userId);
    public String createRefreshToken(int userId);
    public boolean validateAccessToken(String token);
    public boolean validateRefreshToken(String token);
    public int getUserId(String token);
}