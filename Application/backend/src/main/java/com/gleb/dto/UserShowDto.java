package com.gleb.dto;

import com.gleb.data.user.Roles;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
@Data
public class UserShowDto {

    private String username;
    private String firstName;
    private String lastName;
    private LocalDate birthdate;
    private Set<Roles> roles = new HashSet<>();
}
