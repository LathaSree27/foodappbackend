package com.tweats.exceptions;

public class OrderAlreadyCompletedException extends Exception {
    public OrderAlreadyCompletedException() {
        super("This order is already completed!");
    }
}
