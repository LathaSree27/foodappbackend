package com.tweats.exceptions;

public class NoCategoryFoundException extends Exception {
    public NoCategoryFoundException() {
        super("Category doesn't exist for this user");
    }
}
