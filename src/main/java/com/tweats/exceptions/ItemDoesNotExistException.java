package com.tweats.exceptions;

public class ItemDoesNotExistException extends Exception {
    public ItemDoesNotExistException() {
        super("No item exist with this id!");
    }
}
