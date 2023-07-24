package com.gleb.util.wrapper;

public class UserWrapper {
    private final String username;
    private final String roles;

    public UserWrapper(String username, String roles) {
        this.username = username;
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public String getRoles() {
        return roles;
    }
}
