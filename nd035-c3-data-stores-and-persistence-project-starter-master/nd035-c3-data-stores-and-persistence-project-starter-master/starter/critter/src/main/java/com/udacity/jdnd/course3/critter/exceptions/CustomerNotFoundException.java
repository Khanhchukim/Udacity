package com.udacity.jdnd.course3.critter.exceptions;

public class CustomerNotFoundException extends RuntimeException{
    public CustomerNotFoundException() {
    }

    public CustomerNotFoundException(String message) {
        super(message);
    }
}
