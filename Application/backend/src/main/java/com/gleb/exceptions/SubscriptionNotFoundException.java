package com.gleb.exceptions;

public class SubscriptionNotFoundException extends RuntimeException{
    public SubscriptionNotFoundException(String username) {
        super("Subscription with username: " + username + " not found.");
    }
}
