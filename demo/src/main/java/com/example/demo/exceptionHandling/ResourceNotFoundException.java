package com.example.demo.exceptionHandling;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message) {

        super(message);
    }
}