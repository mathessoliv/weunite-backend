package com.example.weuniteauth.handler;

import com.example.weuniteauth.exceptions.BusinessRuleException;
import com.example.weuniteauth.exceptions.DuplicateResourceException;
import com.example.weuniteauth.exceptions.NotFoundResourceException;
import com.example.weuniteauth.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundResourceException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundResourceException(NotFoundResourceException ex) {
        logger.error(ex.getMessage(), ex.getErrorCode());
        ErrorResponse response = new ErrorResponse(ex.getMessage(), ex.getErrorCode());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(DuplicateResourceException ex) {
        logger.error(ex.getMessage(), ex.getErrorCode());
        ErrorResponse response = new ErrorResponse(ex.getMessage(), ex.getErrorCode());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRuleException(BusinessRuleException ex) {
        logger.error(ex.getMessage(), ex.getErrorCode());
        ErrorResponse response = new ErrorResponse(ex.getMessage(), ex.getErrorCode());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
