package com.tweats.exceptions;

public class ImageSizeExceededException extends Exception {
    public ImageSizeExceededException() {
        super("Image size exceeded");
    }
}
