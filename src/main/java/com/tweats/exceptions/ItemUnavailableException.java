package com.tweats.exceptions;

public class ItemUnavailableException extends Exception {
    public ItemUnavailableException() {
        super("Item is not available at this point of time!");
    }
}
