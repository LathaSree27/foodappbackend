package com.tweats.exceptions;

public class UserNotFoundException extends Exception {

    public UserNotFoundException() {
        super("No user found with this email!");
    }
}
