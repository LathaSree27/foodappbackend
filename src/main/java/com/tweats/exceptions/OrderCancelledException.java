package com.tweats.exceptions;

public class OrderCancelledException extends Exception{
    public OrderCancelledException() {
        super("This order is already got cancelled!");
    }
}
