package org.spring.steganography.Exception;

public class UnAuthorizedActionException extends RuntimeException{

    public UnAuthorizedActionException(String message) {
        super(message);
    }

    public UnAuthorizedActionException(String message, Throwable cause) {
        super(message, cause);
    }
}
