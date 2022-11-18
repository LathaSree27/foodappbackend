package com.tweats.handlers;

import com.tweats.controller.response.ErrorResponse;
import com.tweats.exceptions.NoCategoryFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(NoCategoryFoundException.class)
    public ResponseEntity handlerNoCategoryFoundException(NoCategoryFoundException ex){
        ErrorResponse errorResponse = new ErrorResponse("No suitable category is found for this vendor", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
