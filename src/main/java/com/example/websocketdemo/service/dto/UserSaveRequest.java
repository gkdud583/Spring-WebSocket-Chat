package com.example.websocketdemo.service.dto;

public class UserSaveRequest {
    private final String email;
    private final String password;

    public UserSaveRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
