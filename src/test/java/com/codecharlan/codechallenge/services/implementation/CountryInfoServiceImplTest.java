package com.codecharlan.codechallenge.services.implementation;

import com.codecharlan.codechallenge.dtos.response.ApiResponse;
import com.codecharlan.codechallenge.models.CountryInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.mockito.Mockito;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CountryInfoServiceImplTest {
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private CountryInfoServiceImpl countryInfoService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetCountryInfo_Success() {

        String sampleCountry = "SampleCountry";
        String samplePopulationData = "{\"data\":{\"long\":1,\"lat\":2}}";
        String sampleCurrencyData = "{\"data\":{\"currency\":\"USD\",\"iso2\":\"US\",\"iso3\":\"USA\"}}";
        String samplePopulationCounts = "{\"data\":{\"populationCounts\":[{\"year\":2021,\"value\":1000}]}}";
        String sampleCapitalData = "{\"data\":{\"capital\":\"SampleCapital\"}}";

        when(restTemplate.getForEntity(anyString(), any())).thenReturn(
                new ResponseEntity<>(samplePopulationData, HttpStatus.OK),
                new ResponseEntity<>(sampleCurrencyData, HttpStatus.OK),
                new ResponseEntity<>(samplePopulationCounts, HttpStatus.OK),
                new ResponseEntity<>(sampleCapitalData, HttpStatus.OK)
        );

        ApiResponse<CountryInfo> response = countryInfoService.getCountryInfo(sampleCountry);

        assertNotNull(response);
        assertFalse(response.error());
        assertEquals("Country information loaded successfully", response.msg());

        CountryInfo countryInfo = response.data();
        assertNotNull(countryInfo);
        assertEquals("SampleCapital", countryInfo.getCapital());
        assertEquals("USD", countryInfo.getCurrency());
        assertEquals("US", countryInfo.getIso2());
        assertEquals("USA", countryInfo.getIso3());
        assertEquals(1, countryInfo.getLongitude());
        assertEquals(2, countryInfo.getLatitude());

        Map<Integer, Long> populationCounts = countryInfo.getPopulationCounts();
        assertNotNull(populationCounts);
        assertEquals(1, populationCounts.size());
        assertTrue(populationCounts.containsKey(2021));
        assertEquals(1000L, populationCounts.get(2021));
    }

    @Test
    public void testGetCountryInfo_RestClientException() {

        when(restTemplate.getForEntity(anyString(), any())).thenThrow(new RestClientException("RestClientException"));

        RestClientException exception = assertThrows(RestClientException.class,
                () -> countryInfoService.getCountryInfo("SampleCountry"));

        // Assertions
        assertNotNull(exception);
        assertEquals("Failed to retrieve country information", exception.getMessage());
    }

    @Test
    public void testGetCountryInfo_GenericException() {
        when(restTemplate.getForEntity(anyString(), any())).thenThrow(new RuntimeException("GenericException"));

        ApiResponse<CountryInfo> response = countryInfoService.getCountryInfo("SampleCountry");

        assertNotNull(response);
        assertTrue(response.error());
        assertEquals("Failed to retrieve country information", response.msg());
        assertNull(response.data());
    }
}
