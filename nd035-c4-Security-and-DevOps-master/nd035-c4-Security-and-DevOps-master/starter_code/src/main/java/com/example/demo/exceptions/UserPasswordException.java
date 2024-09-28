package com.example.demo.exceptions;

public class UserPasswordException extends RuntimeException {
    public UserPasswordException(String message) {
        super(message);
    }
}
