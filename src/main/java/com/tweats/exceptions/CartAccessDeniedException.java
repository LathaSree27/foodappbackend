package com.tweats.exceptions;

public class CartAccessDeniedException extends Exception {
    public CartAccessDeniedException() {
        super("This cart does not belong to you!");
    }
}
