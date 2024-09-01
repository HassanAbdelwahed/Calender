package com.example.demo.exceptionHandling;

import com.example.demo.util.ResponseData;
import com.example.demo.util.ResponseDataOrError;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseDataOrError<?>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ResponseDataOrError<?> responseData = new ResponseDataOrError<>(ex.getMessage());
        return new ResponseEntity<>(responseData, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseDataOrError<?>> handleBadRequestException(BadRequestException ex) {
        ResponseDataOrError<?> responseData = new ResponseDataOrError<>(ex.getMessage());
        return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
    }

}
