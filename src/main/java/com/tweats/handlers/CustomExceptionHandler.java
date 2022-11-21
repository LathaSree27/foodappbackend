package com.tweats.handlers;

import com.tweats.controller.response.ErrorResponse;
import com.tweats.exceptions.CategoryAlreadyAssignedException;
import com.tweats.exceptions.NoCategoryFoundException;
import com.tweats.exceptions.NotAVendorException;
import com.tweats.exceptions.UserNotFoundException;
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


    @ExceptionHandler(NoCategoryFoundException.class)
    public ResponseEntity handlerNoCategoryFoundException(NoCategoryFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse("No suitable category is found for this vendor", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotAVendorException.class)
    public ResponseEntity handlerNotAVendorException(NotAVendorException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Not a Vendor!", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity handlerUserNotFoundException(UserNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse("User does not Exist!", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CategoryAlreadyAssignedException.class)
    public ResponseEntity handlerCategoryAlreadyAssignedException(CategoryAlreadyAssignedException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Category already Assigned!", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity handlerIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Parameters cannot be blank!", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


}
