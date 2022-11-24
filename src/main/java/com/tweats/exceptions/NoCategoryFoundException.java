package com.tweats.exceptions;

public class NoCategoryFoundException extends Exception {

    public NoCategoryFoundException() {
        super("Category Does not exist with the given id.");
    }
}
