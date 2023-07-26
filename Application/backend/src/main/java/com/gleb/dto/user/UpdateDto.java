package com.gleb.dto.user;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private LocalDateTime updated;
    private String username;
}
