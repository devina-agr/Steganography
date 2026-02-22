package org.spring.steganography.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ImageCapacityExceededException.class)
    public ResponseEntity<ErrorResponse> handleImageCapacity(ImageCapacityExceededException e){
        return buildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidInviteException.class)
    public ResponseEntity<ErrorResponse> handleInvalidInvite(InvalidInviteException e){
        return buildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleTokenExpired(TokenExpiredException e){
        return buildResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UnAuthorizedActionException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnAuthorizedActionException e){
        return buildResponse(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception e){
        return buildResponse("Internal Server Error",HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private ResponseEntity<ErrorResponse> buildResponse(String message, HttpStatus status) {
        ErrorResponse errorResponse=new ErrorResponse(message, status.value(), LocalDateTime.now());
        return new ResponseEntity<>(errorResponse,status);
    }

}
