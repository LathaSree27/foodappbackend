package com.tweats.exceptions;

public class NoOrdersFoundException extends Exception{
    public NoOrdersFoundException() {
        super("No orders found!");
    }
}
