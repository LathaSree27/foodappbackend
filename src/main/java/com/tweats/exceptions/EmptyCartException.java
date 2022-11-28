package com.tweats.exceptions;

public class EmptyCartException extends Exception {
    public EmptyCartException() {
        super("cart is empty!");
    }
}
