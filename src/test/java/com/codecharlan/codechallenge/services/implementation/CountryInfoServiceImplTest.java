package com.codecharlan.codechallenge.services.implementation;


import com.codecharlan.codechallenge.dtos.response.ApiResponse;
import com.codecharlan.codechallenge.models.CountryInfo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CountryInfoServiceImplTest {
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private CountryInfoServiceImpl countryInfoService;
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetCountryInfo_RestClientException() {
        String country = "Albania";
        String positionsApiUrl = "https:mockurl.com/services";

        when(restTemplate.getForEntity(eq(positionsApiUrl), eq(String.class)))
                .thenThrow(new RestClientException("Failed to retrieve data"));

        ApiResponse<CountryInfo> result = countryInfoService.getCountryInfo(country);

        assertTrue(result.error());
        assertNull(result.data());
        assertEquals("Failed to retrieve country information", result.msg());
    }

    @Test
    public void testGetCountryInfo_Exception() {
        String country = "Albania";
        String positionsApiUrl = "https:mockurl.com/services";

        when(restTemplate.getForEntity(eq(positionsApiUrl), eq(String.class)))
                .thenThrow(new RuntimeException("Some exception occurred"));

        ApiResponse<CountryInfo> result = countryInfoService.getCountryInfo(country);

        assertTrue(result.error());
        assertNull(result.data());
        assertEquals("Failed to retrieve country information", result.msg());
    }
}
