package com.codecharlan.codechallenge.services.implementation;

import com.codecharlan.codechallenge.dtos.response.ApiResponse;
import com.codecharlan.codechallenge.exception.CurrencyConversionNotFound;
import com.codecharlan.codechallenge.exception.ResourceNotFoundException;
import com.codecharlan.codechallenge.models.CurrencyConversion;
import com.codecharlan.codechallenge.services.CurrencyConversionService;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

public class CurrencyConversionImplTest {
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private CurrencyConversionImpl currencyConversionService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testConvertCurrency_SuccessfulConversion() {
        // Mock response data from the external API
        String mockApiResponse = "{\"data\":[{\"name\":\"SampleCountry\",\"currency\":\"USD\"}]}";
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(mockApiResponse, HttpStatus.OK));

        // Mock exchange rates
        currencyConversionService.loadExchangeRates();

        // Test data
        String country = "japan";
        BigDecimal amount = BigDecimal.valueOf(100);
        String targetCurrency = "UGX";

        // Perform the currency conversion
        ApiResponse<CurrencyConversion> response = currencyConversionService.convertCurrency(country, amount, targetCurrency);

        // Assertions
        assertNotNull(response);
        assertFalse(response.error());
        assertEquals("Data retrieved Successfully", response.msg());

        CurrencyConversion conversion = response.data();
        assertNotNull(conversion);
        assertEquals("JPY", conversion.getSourceCurrency());
        assertEquals("UGX", conversion.getTargetCurrency());
        assertEquals(amount, conversion.getAmount());
        assertNotNull(conversion.getConvertedAmount());
    }

    @Test
    public void testConvertCurrency_CurrencyConversionNotFound() {
        // Mock response data from the external API
        String mockApiResponse = "{\"data\":[{\"name\":\"SampleCountry\",\"currency\":\"USD\"}]}";
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(mockApiResponse, HttpStatus.OK));

        // Mock exchange rates (no conversion rate available)
        currencyConversionService.loadExchangeRates();

        // Test data
        String country = "SampleCountry";
        BigDecimal amount = BigDecimal.valueOf(100);
        String targetCurrency = "GBP"; // Currency not available in exchange rates

        // Perform the currency conversion
        CurrencyConversionNotFound exception = assertThrows(CurrencyConversionNotFound.class,
                () -> currencyConversionService.convertCurrency(country, amount, targetCurrency));

        // Assertions
        assertNotNull(exception);
        assertEquals("Conversion rate not available for the specified currencies", exception.getMessage());
    }

    @Test
    public void testConvertCurrency_CurrencyConversionNotFoundException() {
        String mockApiResponse = "{\"data\":[]}";
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(mockApiResponse, HttpStatus.OK));


        String country = "ambazonia";
        BigDecimal amount = BigDecimal.valueOf(100);
        String targetCurrency = "EUR";


        CurrencyConversionNotFound exception = assertThrows(CurrencyConversionNotFound.class,
                () -> currencyConversionService.convertCurrency(country, amount, targetCurrency));

        assertNotNull(exception);
        assertEquals("Conversion rate not available for the specified currencies", exception.getMessage());
    }

    @Test
    public void testConvertCurrency_ResourceNotFound() {
        String mockApiResponse = "{\"unexpected\": \"format\"}";
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(mockApiResponse, HttpStatus.OK));

        // Test data
        String country = "nigeria";
        BigDecimal amount = BigDecimal.valueOf(100);
        String targetCurrency = "EUR";

        // Perform the currency conversion
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> currencyConversionService.convertCurrency(country, amount, targetCurrency));

        // Assertions
        assertNotNull(exception);
        assertEquals("Country not found in the response", exception.getMessage());
    }
}
