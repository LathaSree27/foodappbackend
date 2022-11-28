package com.tweats.exceptions;

public class CategoryAlreadyAssignedException extends Exception {

    public CategoryAlreadyAssignedException() {
        super("You can assign only one category to a vendor");
    }
}
