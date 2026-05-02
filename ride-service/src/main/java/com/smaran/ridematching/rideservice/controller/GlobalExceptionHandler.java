package com.smaran.ridematching.rideservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    //To handle invalid request body
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Map<String, String>> handleValidationError(
            MethodArgumentNotValidException exception
    ){
        //Collects all input errors and return them in a readable format
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    //TO handle runtime exceptions
    @ExceptionHandler(RuntimeException.class)
    public  ResponseEntity<Map<String, String>> handleRuntimeException(
            RuntimeException exception
    ){
        log.error("Runtime Exception: {}", exception.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("error", exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
