package com.gleb.validation;

import com.gleb.dto.user.RegisterRequestDto;
import com.gleb.dto.user.UpdateDto;

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
        EMAIL, FIRST_NAME, LAST_NAME, PASSWORD
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

    public static ValidationField validateUpdatedUser(UpdateDto updateDto) {

        if (!isValidName(updateDto.getFirstName())) {
            return ValidationField.FIRST_NAME;
        }

        if (!isValidName(updateDto.getLastName())) {
            return ValidationField.LAST_NAME;
        }

        if (!isValidPassword(updateDto.getPassword())) {
            return ValidationField.PASSWORD;
        }

        if(!isValidEmail(updateDto.getEmail())){
            return ValidationField.EMAIL;
        }

        return null;
    }
}
