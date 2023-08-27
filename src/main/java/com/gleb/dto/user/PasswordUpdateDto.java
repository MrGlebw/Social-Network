package com.gleb.dto.user;

import com.gleb.validation.UserValidator;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PasswordUpdateDto {

    private String password;
    private String newPassword;
    private String newPasswordConfirm;
    private LocalDateTime updated;

    public String getFieldValue(UserValidator.ValidationField field) {
        return switch (field) {
            case PASSWORD -> password;
            case NEW_PASSWORD -> newPassword;
            case NEW_PASSWORD_CONFIRM -> newPasswordConfirm;
            default -> "Unknown field.";
        };
    }

}
