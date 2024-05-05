package com.example.ap2_ex3.api;

public class TokenRequest {
    private String username;
    private String password;
    private String appToken;

    public TokenRequest(String username, String password, String appToken) {
        this.username = username;
        this.password = password;
        this.appToken = appToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
