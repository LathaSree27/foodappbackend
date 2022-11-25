package com.tweats.handlers;

import com.tweats.controller.response.ErrorResponse;
import com.tweats.exceptions.*;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity handleMethodArgumentNotValid(ConstraintViolationException ex) {
        ArrayList<String> details = new ArrayList<>();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            details.add(violation.getMessage());
        }
        ErrorResponse error = new ErrorResponse("Validation Failed", details);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity handleEmptyResultDataAccessException(EmptyResultDataAccessException ex) {
        ErrorResponse error = new ErrorResponse("CartItem not found!", Collections.singletonList("CartItem with given id does not exist!"));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoItemsFoundException.class)
    public ResponseEntity handleNoItemsFoundException(NoItemsFoundException ex) {
        ErrorResponse error = new ErrorResponse("No items found!", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ItemAccessException.class)
    public ResponseEntity handleItemAccessException(ItemAccessException ex) {
        ErrorResponse error = new ErrorResponse("Item access denied!", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ItemDoesNotExistException.class)
    public ResponseEntity handleItemDoesNotExistException(ItemDoesNotExistException ex) {
        ErrorResponse error = new ErrorResponse("Item not found!", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoCategoryFoundException.class)
    public ResponseEntity handleNoCategoryFoundException(NoCategoryFoundException ex) {
        ErrorResponse error = new ErrorResponse("Category Not Found!", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CartItemNotFoundException.class)
    public ResponseEntity handleCartItemNotFoundException(CartItemNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("CartItem Not Found!", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
