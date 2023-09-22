package com.codecharlan.codechallenge.services.implementation;

import com.codecharlan.codechallenge.dtos.response.ApiResponse;
import com.codecharlan.codechallenge.dtos.response.CurrencyDataDto;
import com.codecharlan.codechallenge.exception.CurrencyConversionNotFound;
import com.codecharlan.codechallenge.exception.ResourceNotFoundException;
import com.codecharlan.codechallenge.models.CurrencyConversion;
import com.codecharlan.codechallenge.services.CurrencyConversionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
@Slf4j
@Service

public class CurrencyConversionImpl implements CurrencyConversionService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final Map<String, Double> exchangeRates = new HashMap<>();
    private final String apiUrl = "https://countriesnow.space/api/v0.1";
    private final String currencyApiUrl = apiUrl + "/countries/currency";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
    private ResponseEntity<String> sendGetRequest(String url) {
        HttpEntity<?> requestEntity = new HttpEntity<>(createHeaders());

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                url, HttpMethod.GET, requestEntity, String.class);

        System.out.println("Response Body: " + responseEntity.getBody());

        return responseEntity;
    }
    void loadExchangeRates() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/exchange_rate.csv"));
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] parts = line.split(",");
                String sourceCurrency = parts[0].trim();
                String targetCurrency = parts[1].trim();
                Double rate = Double.valueOf(parts[2].trim());
                exchangeRates.put(sourceCurrency + "_" + targetCurrency, rate);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(exchangeRates);
    }
    public ApiResponse<CurrencyConversion> convertCurrency(String country, BigDecimal amount, String targetCurrency) {
        String fullUrl = "https://countriesnow.space/api/v0.1/countries/currency";
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(fullUrl)
                .queryParam("country", country.trim());

        ResponseEntity<String> response = sendGetRequest(builder.toUriString());

        String responseJson = response.getBody();
        try {
            JsonNode rootNode = objectMapper.readTree(responseJson);

            if (rootNode.has("data") && rootNode.get("data").isArray()) {
                JsonNode dataArray = rootNode.get("data");

                String userInput = country;
                String targetCountryName = userInput.substring(0, 1).toUpperCase() + userInput.substring(1).toLowerCase();
                JsonNode targetCountry = null;

                for (JsonNode countryNode : dataArray) {
                    if (countryNode.has("name") && countryNode.get("name").asText().equals(targetCountryName)) {
                        targetCountry = countryNode;
                        break;
                    }
                }

                if (targetCountry != null) {
                    CurrencyDataDto data = objectMapper.treeToValue(targetCountry, CurrencyDataDto.class);

                    log.info("Calculating the conversion rate");
                    String sourceCurrency = data.getCurrency();
                    System.out.println("Source Country " + sourceCurrency);
                    String conversionKey = sourceCurrency + "_" + targetCurrency;
                    System.out.println("Source Country + Target" + conversionKey);
                    loadExchangeRates();
                    Double rate = exchangeRates.get(conversionKey);

                    if (rate != null) {
                        BigDecimal convertedAmount = amount.multiply(BigDecimal.valueOf(rate));
                        CurrencyConversion result = new CurrencyConversion(sourceCurrency, targetCurrency, amount, convertedAmount);
                        return new ApiResponse<>("Data retrieved Successfully", result, false);
                    } else {
                        throw new ResourceNotFoundException("Country not found in the response");
                    }
                } else {
                    throw new CurrencyConversionNotFound("Conversion rate not available for the specified currencies");
                }
            } else {
                throw new JSONException("Unexpected response format");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error processing response");
        }
    }
}
