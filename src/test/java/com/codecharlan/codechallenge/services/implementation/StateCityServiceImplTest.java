package com.codecharlan.codechallenge.services.implementation;

import com.codecharlan.codechallenge.services.implementation.StateCityServiceImpl;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClientException;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

public class StateCityServiceImplTest {
    @InjectMocks
    private StateCityServiceImpl stateCityService;
    @Mock
    private CloseableHttpClient httpClient;
    @Mock
    private HttpPost citiesRequest;
    @Mock
    private HttpPost stateCitiesRequest;
    @Mock
    private HttpResponse citiesResponse;
    @Mock
    private HttpResponse stateCitiesResponse;
    @Mock
    private StatusLine citiesStatusLine;
    @Mock
    private StatusLine stateCitiesStatusLine;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetCitiesAndState_Success() throws IOException {
        // Mocking HttpClient and responses
        when(httpClient.execute(eq(citiesRequest))).thenReturn((CloseableHttpResponse) citiesResponse);
        when(httpClient.execute(eq(stateCitiesRequest))).thenReturn((CloseableHttpResponse) stateCitiesResponse);
        when(citiesResponse.getStatusLine()).thenReturn(citiesStatusLine);
        when(stateCitiesResponse.getStatusLine()).thenReturn(stateCitiesStatusLine);
        when(citiesStatusLine.getStatusCode()).thenReturn(200);
        when(stateCitiesStatusLine.getStatusCode()).thenReturn(200);

        // Mocking response JSON data
        String citiesResponseString = "{\"data\":{\"cities\":[\"City1\", \"City2\"]}}";
        String stateCitiesResponseString = "{\"data\":[\"State1\", \"State2\"]}";
        when(EntityUtils.toString(citiesResponse.getEntity())).thenReturn(citiesResponseString);
        when(EntityUtils.toString(stateCitiesResponse.getEntity())).thenReturn(stateCitiesResponseString);

        // Test
        JSONObject result = stateCityService.getCitiesAndState("Country");

        assertNotNull(result);
        assertTrue(result.has("country"));
        assertTrue(result.has("states"));
        JSONArray states = result.getJSONArray("states");
        assertEquals(2, states.length());
        assertEquals("City1", states.getString(0));
        assertEquals("City2", states.getString(1));
    }

    @Test
    public void testGetCitiesAndState_CitiesRequestFailed() throws IOException {
        // Mocking HttpClient and responses
        when(httpClient.execute(eq(citiesRequest))).thenReturn((CloseableHttpResponse) citiesResponse);
        when(httpClient.execute(eq(stateCitiesRequest))).thenReturn((CloseableHttpResponse) stateCitiesResponse);
        when(citiesResponse.getStatusLine()).thenReturn(citiesStatusLine);
        when(stateCitiesResponse.getStatusLine()).thenReturn(stateCitiesStatusLine);
        when(citiesStatusLine.getStatusCode()).thenReturn(500); // Simulate a failed request

        assertThrows(RestClientException.class, () -> stateCityService.getCitiesAndState("Country"));
    }

    @Test
    public void testGetCitiesAndState_StateCitiesRequestFailed() throws IOException {
        // Mocking HttpClient and responses
        when(httpClient.execute(eq(citiesRequest))).thenReturn((CloseableHttpResponse) citiesResponse);
        when(httpClient.execute(eq(stateCitiesRequest))).thenReturn((CloseableHttpResponse) stateCitiesResponse);
        when(citiesResponse.getStatusLine()).thenReturn(citiesStatusLine);
        when(stateCitiesResponse.getStatusLine()).thenReturn(stateCitiesStatusLine);
        when(citiesStatusLine.getStatusCode()).thenReturn(200);
        when(stateCitiesStatusLine.getStatusCode()).thenReturn(500); // Simulate a failed request

        assertThrows(RestClientException.class, () -> stateCityService.getCitiesAndState("Country"));
    }

    @Test
    public void testGetCitiesAndState_ExceptionThrown() throws IOException {
        // Mocking HttpClient and responses to throw an exception
        when(httpClient.execute(eq(citiesRequest))).thenThrow(new IOException("IO Exception"));

        assertThrows(RuntimeException.class, () -> stateCityService.getCitiesAndState("Country"));
    }

    @Test
    public void testGetCitiesAndState_InvalidApiResponse() throws IOException {
        // Mocking HttpClient and responses
        when(httpClient.execute(eq(citiesRequest))).thenReturn((CloseableHttpResponse) citiesResponse);
        when(httpClient.execute(eq(stateCitiesRequest))).thenReturn((CloseableHttpResponse) stateCitiesResponse);
        when(citiesResponse.getStatusLine()).thenReturn(citiesStatusLine);
        when(stateCitiesResponse.getStatusLine()).thenReturn(stateCitiesStatusLine);
        when(citiesStatusLine.getStatusCode()).thenReturn(200);
        when(stateCitiesStatusLine.getStatusCode()).thenReturn(200);

        // Mocking response JSON data with an invalid format
        String citiesResponseString = "{\"invalid_key\":{\"cities\":[\"City1\", \"City2\"]}}";
        String stateCitiesResponseString = "{\"data\":[\"State1\", \"State2\"]}";
        when(EntityUtils.toString(citiesResponse.getEntity())).thenReturn(citiesResponseString);
        when(EntityUtils.toString(stateCitiesResponse.getEntity())).thenReturn(stateCitiesResponseString);

        assertThrows(JSONException.class, () -> stateCityService.getCitiesAndState("Country"));
    }
}
