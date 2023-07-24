package com.gleb.data;

import org.springframework.security.core.GrantedAuthority;

public enum Roles implements GrantedAuthority {
    USER, ADMIN, MODERATOR;


    @Override
    public String getAuthority() {
        return "ROLE_" + name(); // Add "ROLE_" prefix to the role name
    }

}
