package com.tweats.handlers;

import com.tweats.controller.response.ErrorResponse;
import com.tweats.exceptions.ImageNotFoundException;
import com.tweats.exceptions.ImageSizeExceededException;
import com.tweats.exceptions.NotAnImageException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;

@ControllerAdvice
public class CustomExceptionHandler {

    @Value("${image.max-file-size}")
    private long maxFileSize;

    private final long MEGABYTE = 1024 * 1024;

    @ExceptionHandler(value = {ImageNotFoundException.class})
    public ResponseEntity handleImageNotFoundException(ImageNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("Image doesn't exists!", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotAnImageException.class)
    public ResponseEntity handleNotAnImageException(NotAnImageException ex) {
        ErrorResponse error = new ErrorResponse("Try uploading image file (JPEG/PNG)", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ImageSizeExceededException.class})
    public ResponseEntity<ErrorResponse> handleImageSizeExceededException(ImageSizeExceededException ex) {
        ErrorResponse error = new ErrorResponse("Try uploading less than " + (maxFileSize / MEGABYTE) + " MB", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({SizeLimitExceededException.class})
    public ResponseEntity<ErrorResponse> handleSizeLimitExceededException(SizeLimitExceededException ex) {
        ErrorResponse error = new ErrorResponse("Try uploading less than " + (maxFileSize / MEGABYTE) + " MB", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}
