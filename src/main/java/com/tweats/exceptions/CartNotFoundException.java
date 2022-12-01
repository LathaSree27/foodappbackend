package com.tweats.exceptions;

public class CartNotFoundException extends Exception {
    public CartNotFoundException() {
        super("Cart does not exist with given id!");
    }
}
