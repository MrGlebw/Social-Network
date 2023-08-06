package com.gleb.dto.user;

import com.gleb.validation.UserValidator;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EmailUpdateDto {
    private String email;
    LocalDateTime updated;

    public String getFieldValue(UserValidator.ValidationField field) {

            if (field == UserValidator.ValidationField.EMAIL) {
                return email;
            } else {
                return "Unknown field.";
            }
    }
}
