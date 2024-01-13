package com.gleb.exceptions;

public class InvalidPasswordException extends RuntimeException {

    public InvalidPasswordException(String password) {
        super("Password: " + password + " is invalid.");
    }
}