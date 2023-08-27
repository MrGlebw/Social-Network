package com.gleb.wrapper;

import lombok.Getter;
@Getter

public class UserWrapper {

    private final String username;

    private final String roles;

    public UserWrapper(String username, String roles) {
        this.username = username;
        this.roles = roles;
    }

}
