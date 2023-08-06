package com.gleb.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gleb.validation.UserValidator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthdate;

    public String getFieldValue(UserValidator.ValidationField field) {
        return switch (field) {
            case EMAIL -> email;
            case FIRST_NAME -> firstName;
            case LAST_NAME -> lastName;
            case PASSWORD -> "Password length must be between 8 and 30 characters. It must contain only letters and numbers.";
            default -> "Unknown field.";
        };
    }

}
