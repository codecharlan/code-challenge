package com.codecharlan.codechallenge.services.implementation;

import com.codecharlan.codechallenge.dtos.response.*;
import com.codecharlan.codechallenge.models.CountryInfo;
import com.codecharlan.codechallenge.services.CountryInfoService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Slf4j
@Service

public class CountryInfoServiceImpl implements CountryInfoService {
    @Override
    public ApiResponse<CountryInfo> getCountryInfo(String country) {
        RestTemplate restTemplate = new RestTemplate();

        String populationApiUrl = "https://countriesnow.space/api/v0.1/countries/population/q?country=" + country;
        String capitalApiUrl = "https://countriesnow.space/api/v0.1/countries/capital/q?country=" + country;
        String positionsApiUrl = "https://countriesnow.space/api/v0.1/countries/positions/q?country=" + country;
        String currencyApiUrl = "https://countriesnow.space/api/v0.1/countries/currency/q?country=" + country;

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);


        try {
            log.info("Making Position API Call");
            ResponseEntity<String> positionsResponse = restTemplate.getForEntity(positionsApiUrl, String.class);
            String positionsData = positionsResponse.getBody();

            JSONObject position = new JSONObject(positionsData);
            JSONObject dataObjectPosition = position.getJSONObject("data");
            int parsedLongData = dataObjectPosition.getInt("long");
            int parsedLatData = dataObjectPosition.getInt("lat");


            log.info("Making Currency API Call");
            ResponseEntity<String> currencyResponse = restTemplate.getForEntity(currencyApiUrl, String.class);
            String currencyData = currencyResponse.getBody();
            System.out.println(currencyData);

            JSONObject currency = new JSONObject(currencyData);
            JSONObject dataObjectCurrency = currency.getJSONObject("data");
            String parsedCurrencyData = dataObjectCurrency.getString("currency");
            String parsedIso2 = dataObjectCurrency.getString("iso2");
            String parsedIso3 = dataObjectCurrency.getString("iso3");

            log.info("Making Population API Call");
            ResponseEntity<String> populationResponse = restTemplate.getForEntity(populationApiUrl, String.class);
            String populationData = populationResponse.getBody();
            System.out.println(populationData);

            JSONObject population = new JSONObject(populationData);
            JSONObject dataObject = population.getJSONObject("data");
            JSONArray populationCounts = dataObject.getJSONArray("populationCounts");

            Map<Integer, Long> parsedPopulationData = new HashMap<>();

            for (int i = 0; i < populationCounts.length(); i++) {
                JSONObject element = populationCounts.getJSONObject(i);
                int year = element.getInt("year");
                long value = element.getLong("value");
                parsedPopulationData.put(year, value);
            }

            log.info("Making Capital API Call");
            ResponseEntity<String> capitalResponse = restTemplate.getForEntity(capitalApiUrl, String.class);
            String capitalData = capitalResponse.getBody();

            JSONObject capital = new JSONObject(capitalData);
            JSONObject dataObjectCapital = capital.getJSONObject("data");
            String parsedCapitalData = dataObjectCapital.getString("capital");

            CountryInfo countryInfo = new CountryInfo();
            countryInfo.setCapital(parsedCapitalData);
            countryInfo.setCurrency(parsedCurrencyData);
            System.out.println(parsedPopulationData);
            countryInfo.setPopulationCounts(parsedPopulationData);
            countryInfo.setLatitude(parsedLatData);
            countryInfo.setLongitude(parsedLongData);
            countryInfo.setIso2(parsedIso2);
            countryInfo.setIso3(parsedIso3);

            return new ApiResponse<>("Country information loaded successfully", countryInfo, false);

        } catch (RestClientException e) {
             e.printStackTrace();
            throw new RestClientException("Failed to retrieve country information");
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse<>("Failed to retrieve country information", null, true);
        }
    }
}