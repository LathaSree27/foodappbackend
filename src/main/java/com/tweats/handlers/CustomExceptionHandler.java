package com.tweats.handlers;

import com.tweats.controller.response.ErrorResponse;
import com.tweats.exceptions.*;
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
        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoItemsFoundException.class)
    public ResponseEntity handleNoItemsFoundException(NoItemsFoundException ex) {
        ErrorResponse error = new ErrorResponse("No items found!", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoOrdersFoundException.class)
    public ResponseEntity handleNoOrdersFoundException(NoOrdersFoundException ex) {
        ErrorResponse error = new ErrorResponse("No orders found!", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity handleOrderNotFoundException(OrderNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("Requested order does not exist!", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OrderCategoryMismatchException.class)
    public ResponseEntity handleOrderCategoryMismatchException(OrderCategoryMismatchException ex) {
        ErrorResponse error = new ErrorResponse("Requested order does not belong to this category!", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OrderCancelledException.class)
    public ResponseEntity handleOrderCancelledException(OrderCancelledException ex) {
        ErrorResponse error = new ErrorResponse("Requested order is cancelled already!", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }
}
