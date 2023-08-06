package com.gleb.validation;

import com.gleb.dto.user.PasswordUpdateDto;
import com.gleb.dto.user.RegisterRequestDto;
import com.gleb.dto.user.FullUpdateDto;

public class UserValidator {
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    }

    public static boolean isValidName(String name) {
        return name != null && name.matches("^[A-Z][a-z]*$");
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    public enum ValidationField {
        EMAIL, FIRST_NAME, LAST_NAME, PASSWORD, NEW_PASSWORD, NEW_PASSWORD_CONFIRM
    }

    public static ValidationField validateRegisteredUser(RegisterRequestDto registerRequestDto) {
        if (!isValidEmail(registerRequestDto.getEmail())) {
            return ValidationField.EMAIL;
        }

        if (!isValidName(registerRequestDto.getFirstName())) {
            return ValidationField.FIRST_NAME;
        }

        if (!isValidName(registerRequestDto.getLastName())) {
            return ValidationField.LAST_NAME;
        }

        if (!isValidPassword(registerRequestDto.getPassword())) {
            return ValidationField.PASSWORD;
        }

        return null;
    }

    public static ValidationField validateFullUpdatedUser(FullUpdateDto fullUpdateDto) {

        if (!isValidName(fullUpdateDto.getFirstName())) {
            return ValidationField.FIRST_NAME;
        }

        if (!isValidName(fullUpdateDto.getLastName())) {
            return ValidationField.LAST_NAME;
        }


        if (!isValidEmail(fullUpdateDto.getEmail())) {
            return ValidationField.EMAIL;
        }

        return null;
    }

    public static ValidationField validatePasswordUpdate(PasswordUpdateDto passwordUpdateDto) {

        if (!isValidPassword(passwordUpdateDto.getPassword())) {
            return ValidationField.PASSWORD;
        }

        if (!isValidPassword(passwordUpdateDto.getNewPassword())) {
            return ValidationField.NEW_PASSWORD;
        }

        if (!isValidPassword(passwordUpdateDto.getNewPasswordConfirm())) {
            return ValidationField.NEW_PASSWORD_CONFIRM;
        }

        if (!passwordUpdateDto.getNewPassword().equals(passwordUpdateDto.getNewPasswordConfirm())) {
            return ValidationField.NEW_PASSWORD_CONFIRM;
        }

        return null;
    }

    public static ValidationField validateFirstAndLastName(String firstName, String lastName) {
        if (!isValidName(firstName)) {
            return ValidationField.FIRST_NAME;
        }

        if (!isValidName(lastName)) {
            return ValidationField.LAST_NAME;
        }

        return null;
    }

}
