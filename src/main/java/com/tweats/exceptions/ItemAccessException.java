package com.tweats.exceptions;

public class ItemAccessException extends Exception {
    public ItemAccessException() {
        super("Can't update availability of an item of a different category");
    }
}
