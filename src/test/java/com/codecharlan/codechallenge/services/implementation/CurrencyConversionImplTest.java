package com.codecharlan.codechallenge.services.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CurrencyConversionImplTest {
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private CurrencyConversionImpl currencyConversion;


    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        File exchangeRateFile = File.createTempFile("exchange_rates", ".csv");
        exchangeRateFile.deleteOnExit();

        Files.write(
                Path.of(exchangeRateFile.toURI()),
                "sourceCurrency,targetCurrency,rate\nUSD,GBP,0.75\nEUR,GBP,0.88\n".getBytes(),
                StandardOpenOption.WRITE
        );

        currencyConversion.setExchangeRateFile(exchangeRateFile.getAbsolutePath());
        currencyConversion.initializeExchangeCalculation();
    }
    @Test
    public void testInitializeExchangeCalculation() {
        assert currencyConversion.getExchangeRates().size() == 2;
        assert currencyConversion.getExchangeRates().get("USD_GBP") == 0.75;
        assert currencyConversion.getExchangeRates().get("EUR_GBP") == 0.88;
    }
    @Test
    public void testSendGetRequest() {
        String testUrl = "https://mockapiexample.com/api/currency-resource";

        String mockResponse = "{\"data\": \"mocked data\"}";
        ResponseEntity<String> mockResponseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        when(restTemplate.exchange(eq(testUrl), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(mockResponseEntity);
        ResponseEntity<String> response = currencyConversion.sendGetRequest(testUrl);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(restTemplate, times(1)).exchange(eq(testUrl), eq(HttpMethod.GET), any(), eq(String.class));
    }

    @Test
    public void testCreateHeaders() {
        HttpHeaders headers = currencyConversion.createHeaders();
        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
    }
}
