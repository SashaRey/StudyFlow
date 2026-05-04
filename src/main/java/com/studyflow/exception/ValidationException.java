package com.studyflow.exception;

/**
 * Indicates invalid user input or domain validation failure.
 */
public class ValidationException extends AppException {

    public ValidationException(String message) {
        super(message);
    }
}

