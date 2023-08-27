package com.gleb.exceptions;

public class SubscriptionAlreadyExistsException extends RuntimeException {

    public SubscriptionAlreadyExistsException(String username) {
        super("Subscription for user: " + username + " already exists.");
    }
}
