package com.codecharlan.codechallenge.services.implementation;

import com.codecharlan.codechallenge.services.CityService;
import com.codecharlan.codechallenge.dtos.response.ApiResponse;
import com.codecharlan.codechallenge.models.CountryPopulationData;
import com.codecharlan.codechallenge.utils.CityComparator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
@RequiredArgsConstructor
@Service
public class CityServiceImpl implements CityService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${base.api.url}")
    private String baseApiUrl;
    private final List<String> countries = Arrays.asList("Italy", "New Zealand", "Ghana");
    @Override
    public ApiResponse<List<CountryPopulationData>> getMostPopulatedCities(int N) {
        if (N <= 0) {
            throw new IllegalArgumentException("N must be a positive integer");
        }

        List<CountryPopulationData> collatedCountryPopulationData = new ArrayList<>();

        for (String country : countries) {
            String apiUrl = baseApiUrl + "/countries/population/cities/filter/q?country="+country+"&limit=1000&order=dsc";
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(
                    apiUrl, String.class);

            String responseJson = responseEntity.getBody();

            try {
                JsonNode rootNode = objectMapper.readTree(responseJson);

                if (rootNode.has("data") && rootNode.get("data").isArray()) {
                    JsonNode dataArray = rootNode.get("data");

                    CountryPopulationData[] cities = objectMapper.treeToValue(dataArray, CountryPopulationData[].class);

                    collatedCountryPopulationData.addAll(Arrays.asList(cities));
                } else {
                    throw new IOException("Invalid API response format");
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Invalid API response format");
            }
        }

        collatedCountryPopulationData.sort(new CityComparator());

        return new ApiResponse<>("Most populated cities loaded successfully", collatedCountryPopulationData.subList(0, N), false);
    }
}
