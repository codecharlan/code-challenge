package com.codecharlan.codechallenge.services.implementation;

import com.codecharlan.codechallenge.dtos.response.ApiResponse;
import com.codecharlan.codechallenge.dtos.response.CurrencyDataDto;
import com.codecharlan.codechallenge.exception.CurrencyConversionNotFound;
import com.codecharlan.codechallenge.exception.ResourceNotFoundException;
import com.codecharlan.codechallenge.models.CurrencyConversion;
import com.codecharlan.codechallenge.services.CurrencyConversionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
@RequiredArgsConstructor
@Service


public class CurrencyConversionImpl implements CurrencyConversionService {
    private final RestTemplate restTemplate;
    private final Map<String, Double> exchangeRates = new HashMap<>();
    private final ObjectMapper objectMapper;

    @Value("${exchange.rate.file}")
    private String exchangeRateFile;

    @Value("${base.api.url}")
    private String baseApiUrl;

    @PostConstruct
    public void initializeExchangeCalculation() {
        loadExchangeRates();
    }
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
    private ResponseEntity<String> sendGetRequest(String url) {
        HttpEntity<?> requestEntity = new HttpEntity<>(createHeaders());

        return restTemplate.exchange(
                url, HttpMethod.GET, requestEntity, String.class);
    }

    private void loadExchangeRates() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(exchangeRateFile));
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
    }
    public ApiResponse<CurrencyConversion> convertCurrency(String country, BigDecimal amount, String targetCurrency) {
        String currencyUrl = baseApiUrl + "/countries/currency";

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(currencyUrl)
                .queryParam("country", country.trim());

        ResponseEntity<String> response = sendGetRequest(builder.toUriString());
        log.info("CurrencyConversionImpl getCountryCurrency :: [{}]", response);

        String responseJson = response.getBody();
        try {
            JsonNode rootNode = objectMapper.readTree(responseJson);

            if (rootNode.has("data") && rootNode.get("data").isArray()) {
                JsonNode dataArray = rootNode.get("data");

                String targetCountryName = country.substring(0, 1).toUpperCase() + country.substring(1).toLowerCase();
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
                    String conversionKey = sourceCurrency + "_" + targetCurrency;
                    initializeExchangeCalculation();
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
