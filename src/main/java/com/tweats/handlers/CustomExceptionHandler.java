package com.tweats.handlers;

import com.tweats.controller.response.ErrorResponse;
import com.tweats.exceptions.*;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${image.max-file-size}")
    private long maxFileSize;

    private final long MEGABYTE = 1024 * 1024;

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(ConstraintViolationException ex) {
        ArrayList<String> details = new ArrayList<>();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            details.add(violation.getMessage());
        }
        ErrorResponse error = new ErrorResponse("Validation Failed", details);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoItemsFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoItemsFoundException(NoItemsFoundException ex) {
        ErrorResponse error = new ErrorResponse("No items found!", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoOrdersFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoOrdersFoundException(NoOrdersFoundException ex) {
        ErrorResponse error = new ErrorResponse("No orders found!", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFoundException(OrderNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("Requested order does not exist!", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OrderCategoryMismatchException.class)
    public ResponseEntity<ErrorResponse> handleOrderCategoryMismatchException(OrderCategoryMismatchException ex) {
        ErrorResponse error = new ErrorResponse("Requested order does not belong to this category!", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OrderCancelledException.class)
    public ResponseEntity<ErrorResponse> handleOrderCancelledException(OrderCancelledException ex) {
        ErrorResponse error = new ErrorResponse("Requested order is cancelled already!", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ItemAccessException.class)
    public ResponseEntity<ErrorResponse> handleItemAccessException(ItemAccessException ex) {
        ErrorResponse error = new ErrorResponse("Item access denied!", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ItemDoesNotExistException.class)
    public ResponseEntity<ErrorResponse> handleItemDoesNotExistException(ItemDoesNotExistException ex) {
        ErrorResponse error = new ErrorResponse("Item not found!", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {ImageNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleImageNotFoundException(ImageNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("Image doesn't exists!", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotAnImageException.class)
    public ResponseEntity<ErrorResponse> handleNotAnImageException(NotAnImageException ex) {
        ErrorResponse error = new ErrorResponse("Try uploading image file (JPEG/PNG)", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
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

    @ExceptionHandler(NoCategoryFoundException.class)
    public ResponseEntity<ErrorResponse> handlerNoCategoryFoundException(NoCategoryFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse("No category found!!", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotAVendorException.class)
    public ResponseEntity<ErrorResponse> handlerNotAVendorException(NotAVendorException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Not a Vendor!", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlerUserNotFoundException(UserNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse("User does not Exist!", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CategoryAlreadyAssignedException.class)
    public ResponseEntity<ErrorResponse> handlerCategoryAlreadyAssignedException(CategoryAlreadyAssignedException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Category already Assigned!", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(CartItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCartItemNotFoundException(CartItemNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("CartItem Not Found!", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmptyCartException.class)
    public ResponseEntity<ErrorResponse> handleEmptyCartException(EmptyCartException ex) {
        ErrorResponse error = new ErrorResponse("your cart is empty!", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CartAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleCartAccessException(CartAccessDeniedException ex) {
        ErrorResponse error = new ErrorResponse("Cart access denied!", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCartNotFoundException(CartNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("Cart not found!", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ItemUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleItemIsNotAvailableException(ItemUnavailableException ex) {
        ErrorResponse error = new ErrorResponse("Item not available!", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OrderAlreadyCompletedException.class)
    public ResponseEntity<ErrorResponse> handleOrderAlreadyCompletedException(OrderAlreadyCompletedException ex) {
        ErrorResponse error = new ErrorResponse("Order completed!", Collections.singletonList(ex.getMessage()));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}

