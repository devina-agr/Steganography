package org.spring.steganography.Exception;

public class ImageCapacityExceededException extends RuntimeException{

    public ImageCapacityExceededException(String message){
        super(message);
    }

    public ImageCapacityExceededException(String message, Throwable cause){
        super(message,cause);
    }


}
