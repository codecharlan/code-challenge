package com.codecharlan.codechallenge.services.implementation;

import com.codecharlan.codechallenge.dtos.response.ApiResponse;
import com.codecharlan.codechallenge.models.CountryPopulationData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
public class CityServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CityServiceImpl cityService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetMostPopulatedCitiesWithValidResponse() throws IOException {
        String validApiResponse = "{ \"data\": [ { \"city\": \"City1\", \"populationCounts\": [ { \"year\": 2022, \"value\": 1000 } ] } ] }";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(validApiResponse, HttpStatus.OK);
        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(), eq(String.class)))
                .thenReturn(responseEntity);

        ApiResponse<List<CountryPopulationData>> apiResponse = cityService.getMostPopulatedCities(1);


        assertNotNull(apiResponse);
        assertFalse(apiResponse.error());
        assertEquals("Most populated cities loaded successfully", apiResponse.msg());
        List<CountryPopulationData> populationDataList = apiResponse.data();
        assertNotNull(populationDataList);
        assertEquals(1, populationDataList.size());
        CountryPopulationData cityData = populationDataList.get(0);
        assertEquals("City1", cityData.getCity());
        assertEquals(1, cityData.getPopulationCounts().size());
        assertEquals(String.valueOf(2022), cityData.getPopulationCounts().get(0).getYear());
        assertEquals(String.valueOf(1000), cityData.getPopulationCounts().get(0).getValue());
    }

    @Test
    @DisplayName("Test With Zero Limit")
    public void testGetMostPopulatedCities() {
        assertThrows(IllegalArgumentException.class, () -> cityService.getMostPopulatedCities(0));
    }

    @Test
    @DisplayName("Test With Empty Data Array")
    public void testGetMostPopulatedCitiesArray() throws IOException {
        String emptyDataArrayResponse = "{ \"data\": [] }";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(emptyDataArrayResponse, HttpStatus.OK);
        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(), eq(String.class)))
                .thenReturn(responseEntity);

        ApiResponse<List<CountryPopulationData>> apiResponse = cityService.getMostPopulatedCities(1);

        assertNotNull(apiResponse);
        assertFalse(apiResponse.error());
        assertEquals("Most populated cities loaded successfully", apiResponse.msg());
        List<CountryPopulationData> populationDataList = apiResponse.data();
        assertNotNull(populationDataList);
        assertTrue(populationDataList.isEmpty());
    }

    @Test
    @DisplayName("Test service method with a negative N")
    public void testGetMostPopulatedCitiesWithNegativeLimit() {
        assertThrows(IllegalArgumentException.class, () -> cityService.getMostPopulatedCities(-1));
    }

}
