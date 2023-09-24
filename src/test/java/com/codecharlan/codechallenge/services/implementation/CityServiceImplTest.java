package com.codecharlan.codechallenge.services.implementation;

import com.codecharlan.codechallenge.dtos.response.ApiResponse;
import com.codecharlan.codechallenge.models.CountryPopulationData;
import com.codecharlan.codechallenge.services.CityService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class CityServiceImplTest {
    private CityService cityService;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        cityService = new CityServiceImpl(restTemplate, objectMapper);
    }
    @Test
    public void testGetMostPopulatedCities() throws IOException {
        ResponseEntity<String> mockResponse = new ResponseEntity<>("{ \"data\": [ mock data] }", HttpStatus.OK);
        Mockito.when(restTemplate.getForEntity(any(String.class), eq(String.class))).thenReturn(mockResponse);

        JsonNode mockJsonNode = Mockito.mock(JsonNode.class);
        Mockito.when(objectMapper.readTree(any(String.class))).thenReturn(mockJsonNode);

        Mockito.when(mockJsonNode.has("data")).thenReturn(true);
        Mockito.when(mockJsonNode.get("data")).thenReturn(Mockito.mock(JsonNode.class));
        Mockito.when(mockJsonNode.get("data").isArray()).thenReturn(true);

        CountryPopulationData[] mockCities = new CountryPopulationData[2];
        Mockito.when(objectMapper.treeToValue(mockJsonNode.get("data"), CountryPopulationData[].class)).thenReturn(mockCities);

        ApiResponse<List<CountryPopulationData>> response = cityService.getMostPopulatedCities(5);

        assertNotNull(response);
        assertEquals("Most populated cities loaded successfully", response.msg());
        assertEquals(5, response.data().size());
    }

}
