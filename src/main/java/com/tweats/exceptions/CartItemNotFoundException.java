package com.tweats.exceptions;

public class CartItemNotFoundException extends Exception {

    public CartItemNotFoundException() {
        super("Cart item does not exists in your cart");
    }
}
