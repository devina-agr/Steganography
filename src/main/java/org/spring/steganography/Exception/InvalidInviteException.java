package org.spring.steganography.Exception;

public class InvalidInviteException extends RuntimeException{

    public InvalidInviteException(String message) {
        super(message);
    }

    public InvalidInviteException(String message, Throwable cause) {
        super(message, cause);
    }
}
