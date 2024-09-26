package com.udacity.jdnd.course3.critter.exceptions;

public class PetWithoutOwnerException extends RuntimeException {
    public PetWithoutOwnerException() {
    }

    public PetWithoutOwnerException(String message) {
        super(message);
    }
}
