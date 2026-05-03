package com.studyflow.exception;

/**
 * Indicates a database access error in the repository layer.
 */
public class DatabaseException extends AppException {

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}

