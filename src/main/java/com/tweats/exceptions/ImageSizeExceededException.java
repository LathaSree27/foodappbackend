package com.tweats.exceptions;

public class ImageSizeExceededException extends Exception {

    public ImageSizeExceededException() {
        super("Image size cannot be greater than 3MB!");
    }
}
