package com.example.users;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    private String token;
    private String userId;
    private String role;

    @SerializedName("name")
    private String name;

    @SerializedName("username")
    private String username;

    public String getToken() { return token; }
    public String getUserId() { return userId; }
    public String getRole() { return role; }

    public String getStudentName() {
        if (name != null && !name.isEmpty()) return name;
        if (username != null && !username.isEmpty()) return username;
        return "";
    }
}