package com.tweats.exceptions;

public class NoItemsFoundException extends Exception {
    public NoItemsFoundException() {
        super("No items present in this category!");
    }
}
