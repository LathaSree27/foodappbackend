package com.tweats.exceptions;

public class NotAVendorException extends Exception {
    public NotAVendorException() {
        super("No vendor account exists with the given email_id!");
    }
}
