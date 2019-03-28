package com.jevin.jmart.models;

public class JwtResponse {

    private String accessToken;
    private String username;
    private int userId;

    public JwtResponse() {
    }

    public JwtResponse(String accessToken, String username, int userId) {
        this.accessToken = accessToken;
        this.username = username;
        this.userId = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
