package com.udacity.jdnd.course3.critter.exceptions;

public class InvalidScheduleException extends RuntimeException {
    public InvalidScheduleException() {
    }

    public InvalidScheduleException(String message) {
        super(message);
    }
}
