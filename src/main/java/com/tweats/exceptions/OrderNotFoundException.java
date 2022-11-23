package com.tweats.exceptions;

public class OrderNotFoundException extends Exception{
    public OrderNotFoundException() {
        super("order not found!!");
    }
}
