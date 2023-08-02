package com.gleb.dto.user;

import com.gleb.validation.UserValidator;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private LocalDateTime updated;


    public String getFieldValue(UserValidator.ValidationField field) {
        return switch (field) {
            case EMAIL -> email;
            case FIRST_NAME -> firstName;
            case LAST_NAME -> lastName;
            case PASSWORD -> "Password must be at least 8 characters long.";
            default -> "Unknown field.";
        };
    }
}
