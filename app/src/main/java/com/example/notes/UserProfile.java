package com.example.notes;

public class UserProfile {
    public String email;
    public String username;

    public UserProfile(String email, String username) {
        this.email = email;
        this.username = username;
    }

    public UserProfile() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
