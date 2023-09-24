package com.codecharlan.codechallenge.services.implementation;

import com.codecharlan.codechallenge.dtos.response.ApiResponse;
import com.codecharlan.codechallenge.models.CountryInfo;
import com.codecharlan.codechallenge.services.CountryInfoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class CountryInfoServiceImpl implements CountryInfoService {

    private final RestTemplate restTemplate;

    @Value("${base.api.url}")
    private String baseApiUrl;

    @Value("${api.population.url}")
    private String populationApiPath;

    @Value("${api.capital.url}")
    private String capitalApiPath;

    @Value("${api.positions.url}")
    private String positionsApiPath;

    @Value("${api.currency.url}")
    private String currencyApiPath;

    @Override
    public ApiResponse<CountryInfo> getCountryInfo(String country) {
        String populationApiUrl = baseApiUrl + populationApiPath + "?country=" + country;
        String capitalApiUrl = baseApiUrl + capitalApiPath + "?country=" + country;
        String positionsApiUrl = baseApiUrl + positionsApiPath + "?country=" + country;
        String currencyApiUrl = baseApiUrl + currencyApiPath + "?country=" + country;


        try {
            ResponseEntity<String> positionsResponse = restTemplate.getForEntity(positionsApiUrl, String.class);
            log.info("CountryInfoServiceImpl getPositionApiCall :: [{}]", positionsResponse);
            String positionsData = positionsResponse.getBody();

            JSONObject position = new JSONObject(positionsData);
            JSONObject dataObjectPosition = position.getJSONObject("data");
            int parsedLongData = dataObjectPosition.getInt("long");
            int parsedLatData = dataObjectPosition.getInt("lat");

            ResponseEntity<String> currencyResponse = restTemplate.getForEntity(currencyApiUrl, String.class);
            log.info("CountryInfoServiceImpl getCurrencyApiCall :: [{}]", currencyResponse);
            String currencyData = currencyResponse.getBody();

            JSONObject currency = new JSONObject(currencyData);
            JSONObject dataObjectCurrency = currency.getJSONObject("data");
            String parsedCurrencyData = dataObjectCurrency.getString("currency");
            String parsedIso2 = dataObjectCurrency.getString("iso2");
            String parsedIso3 = dataObjectCurrency.getString("iso3");

            ResponseEntity<String> populationResponse = restTemplate.getForEntity(populationApiUrl, String.class);
            log.info("CountryInfoServiceImpl getPopulationApiCall :: [{}]", populationResponse);
            String populationData = populationResponse.getBody();

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

            ResponseEntity<String> capitalResponse = restTemplate.getForEntity(capitalApiUrl, String.class);
            log.info("CountryInfoServiceImpl getCapitalApiCall :: [{}]", capitalResponse);
            String capitalData = capitalResponse.getBody();

            JSONObject capital = new JSONObject(capitalData);
            JSONObject dataObjectCapital = capital.getJSONObject("data");
            String parsedCapitalData = dataObjectCapital.getString("capital");

            CountryInfo countryInfo = new CountryInfo();
            countryInfo.setCapital(parsedCapitalData);
            countryInfo.setCurrency(parsedCurrencyData);
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
