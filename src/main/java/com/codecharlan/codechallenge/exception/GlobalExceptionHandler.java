package com.codecharlan.codechallenge.exception;

import com.codecharlan.codechallenge.dtos.response.ApiResponse;
import org.json.JSONException;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(JSONException.class)
    public ResponseEntity<ApiResponse<String>> handleJsonException(JSONException e) {
        return new ResponseEntity<>(new ApiResponse<>(e.getLocalizedMessage(), null, true), NOT_ACCEPTABLE);
    }
    @ExceptionHandler(RestClientException.class)
    @ResponseStatus(NO_CONTENT)
    public ResponseEntity<ApiResponse<String>> handleRestClientException(RestClientException e) {
        return new ResponseEntity<>(new ApiResponse<>(e.getLocalizedMessage(), null, true), NO_CONTENT);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgumentException(IllegalArgumentException e) {
        return new ResponseEntity<>(new ApiResponse<>(e.getLocalizedMessage(), null, true), BAD_REQUEST);
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ResponseEntity<ApiResponse<String>> handleResourceNotFoundException(ResourceNotFoundException e) {
        return new ResponseEntity<>(new ApiResponse<>(e.getLocalizedMessage(), null, true), NOT_FOUND);
    }
    @ExceptionHandler(CurrencyConversionNotFound.class)
    @ResponseStatus(NOT_FOUND)
    public ResponseEntity<ApiResponse<String>> handleIllegalStateException(CurrencyConversionNotFound e) {
        return new ResponseEntity<>(new ApiResponse<>(e.getLocalizedMessage(), null, true), NOT_FOUND);
    }
}
