package com.codecharlan.codechallenge.services.implementation;

import com.codecharlan.codechallenge.services.StateCityService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StateCityServiceImplTest {
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private StateCityServiceImpl stateCityService;
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testGetCitiesAndState_Success() {
        String country = "{\"country\": \"USA\"}";
        String citiesEndpoint = "https://example.com/api/countries";
        String stateCitiesEndpoint = "https://example.com/api/countries/state/cities/q";

        JSONObject citiesData = new JSONObject();
        JSONArray dataArray = new JSONArray();
        JSONObject countryData = new JSONObject();
        countryData.put("country", "USA");
        JSONArray citiesArray = new JSONArray();
        citiesArray.put("New York");
        citiesData.put("data", dataArray);
        dataArray.put(countryData);
        countryData.put("cities", citiesArray);

        ResponseEntity<String> citiesResponseEntity = new ResponseEntity<>(citiesData.toString(), HttpStatus.OK);
        when(restTemplate.getForEntity(citiesEndpoint, String.class)).thenReturn(citiesResponseEntity);

        JSONObject stateCitiesData = new JSONObject();
        JSONArray statesArray = new JSONArray();
        stateCitiesData.put("data", statesArray);

        ResponseEntity<String> stateCitiesResponseEntity = new ResponseEntity<>(stateCitiesData.toString(), HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(stateCitiesResponseEntity);

        JSONObject result = stateCityService.getCitiesAndState(country);

        assertNotNull(result);
        assertEquals("USA", result.getString("country"));

        assertFalse(result.has("states"));

        for (Object city : result.getJSONArray("cities")) {
            verify(restTemplate, times(1)).getForEntity(
                    stateCitiesEndpoint + "?state=" + city + "&country=USA", String.class);
        }
    }
}
