package com.akraness.akranesswaitlist.exception;

import com.akraness.akranesswaitlist.config.CustomResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<CustomResponse> handleUserNotFoundException(UserNotFoundException ex) {
        CustomResponse response = CustomResponse.builder()
                .status(HttpStatus.BAD_REQUEST.name())
                .error(ex.getMessage())
                .build();
        return ResponseEntity.badRequest().body(response);
    }
}
