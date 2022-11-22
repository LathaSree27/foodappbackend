package com.tweats.exceptions;

public class ItemAccessException extends Exception {
    public ItemAccessException() {
        super("Cannot update item of different category");
    }
}
