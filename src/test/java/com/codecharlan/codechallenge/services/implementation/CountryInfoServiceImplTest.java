package com.codecharlan.codechallenge.services.implementation;

import com.codecharlan.codechallenge.dtos.response.ApiResponse;
import com.codecharlan.codechallenge.models.CountryInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class CountryInfoServiceImplTest {
    private CountryInfoServiceImpl countryInfoService;
    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        restTemplate = Mockito.mock(RestTemplate.class);
        countryInfoService = new CountryInfoServiceImpl(restTemplate);
    }

    @Test
    public void testGetCountryInfoException() {
        when(restTemplate.getForEntity(any(String.class), eq(String.class)))
                .thenThrow(new RuntimeException("Failed to retrieve country information"));

        ApiResponse<CountryInfo> response = countryInfoService.getCountryInfo("USA");

        assertNotNull(response);
        assertTrue(response.error());
        assertEquals("Failed to retrieve country information", response.msg());
        assertNull(response.data());
    }
}
