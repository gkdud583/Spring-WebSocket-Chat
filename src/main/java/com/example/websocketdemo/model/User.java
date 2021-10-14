package com.example.websocketdemo.model;

public class User {
    private String email;
    private String password;
    private String auth;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.auth = "ROLE_USER";
}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }
}
