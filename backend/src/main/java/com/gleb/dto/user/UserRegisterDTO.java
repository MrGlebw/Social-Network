package com.gleb.dto.user;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRegisterDTO {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String surname;
    private LocalDate birthDate;
}
