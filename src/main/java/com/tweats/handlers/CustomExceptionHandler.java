package com.tweats.handlers;

import com.tweats.controller.response.ErrorResponse;
import com.tweats.exceptions.ImageNotFoundException;
import com.tweats.exceptions.ImageSizeExceededException;
import com.tweats.exceptions.NotAnImageException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @Value("${image.max-file-size}")
    private long maxFileSize;

    private final long MEGABYTE = 1024*1024;

    @NotNull
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, @NotNull HttpHeaders headers, @NotNull HttpStatus status, @NotNull WebRequest request) {
        List<String> details = new ArrayList<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            details.add(error.getDefaultMessage());
        }
        ErrorResponse error = new ErrorResponse("Validation Failed", details);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

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
