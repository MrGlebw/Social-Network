package com.gleb.dto.user;

import com.gleb.validation.UserValidator;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FirstAndLastnameUpdateDto {

    private String firstName;
    private String lastName;
    private LocalDateTime updated;

    public String getFieldValue(UserValidator.ValidationField field) {
        return switch (field) {
            case FIRST_NAME -> firstName;
            case LAST_NAME -> lastName;
            default -> "Unknown field.";
        };


    }
}
