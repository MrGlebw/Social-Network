package com.gleb.exceptions;


public class PostNotFoundException extends RuntimeException {

    public PostNotFoundException(Integer id) {
        super("Post:" + id + " is not found.");
    }

}
