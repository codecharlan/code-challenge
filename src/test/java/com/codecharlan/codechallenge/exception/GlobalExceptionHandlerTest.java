package com.codecharlan.codechallenge.exception;

import com.codecharlan.codechallenge.dtos.response.ApiResponse;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    public void testHandleJsonException() {
        JSONException exception = new JSONException("JSON parsing error");

        ResponseEntity<ApiResponse<String>> response = globalExceptionHandler.handleJsonException(exception);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
        ApiResponse<String> apiResponse = response.getBody();
        assert apiResponse != null;
        assertTrue(apiResponse.error());
        assertEquals("JSON parsing error", apiResponse.msg());
    }

    @Test
    public void testHandleRestClientException() {
        RestClientException exception = new RestClientException("REST client error");

        ResponseEntity<ApiResponse<String>> response = globalExceptionHandler.handleRestClientException(exception);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        ApiResponse<String> apiResponse = response.getBody();
        assert apiResponse != null;
        assertTrue(apiResponse.error());
        assertEquals("REST client error", apiResponse.msg());
    }

    @Test
    public void testHandleIllegalArgumentException() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        ResponseEntity<ApiResponse<String>> response = globalExceptionHandler.handleIllegalArgumentException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiResponse<String> apiResponse = response.getBody();
        assert apiResponse != null;
        assertTrue(apiResponse.error());
        assertEquals("Invalid argument", apiResponse.msg());
    }

    @Test
    public void testHandleResourceNotFoundException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");

        ResponseEntity<ApiResponse<String>> response = globalExceptionHandler.handleResourceNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ApiResponse<String> apiResponse = response.getBody();
        assert apiResponse != null;
        assertTrue(apiResponse.error());
        assertEquals("Resource not found", apiResponse.msg());
    }

    @Test
    public void testHandleCurrencyConversionNotFound() {
        CurrencyConversionNotFound exception = new CurrencyConversionNotFound("Currency conversion not found");

        ResponseEntity<ApiResponse<String>> response = globalExceptionHandler.handleIllegalStateException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ApiResponse<String> apiResponse = response.getBody();
        assert apiResponse != null;
        assertTrue(apiResponse.error());
        assertEquals("Currency conversion not found", apiResponse.msg());
    }
}
