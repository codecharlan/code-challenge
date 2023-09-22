package com.codecharlan.codechallenge.services.implementation;

import com.codecharlan.codechallenge.dtos.response.ApiResponse;
import com.codecharlan.codechallenge.utils.CityComparator;
import com.codecharlan.codechallenge.models.CountryPopulationData;
import com.codecharlan.codechallenge.services.CityService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CityServiceImpl implements CityService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ApiResponse<List<CountryPopulationData>> getMostPopulatedCities(int N) {
        if (N <= 0) {
            throw new IllegalArgumentException("N must be a positive integer");
        }
        String apiUrl = "https://countriesnow.space/api/v0.1";
        String cityApiUrl = apiUrl + "/countries/population/cities?sortBy=population&order=desc&limit=" + N;

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                cityApiUrl, HttpMethod.GET, null, String.class);

        String responseJson = responseEntity.getBody();

        try {
            JsonNode rootNode = objectMapper.readTree(responseJson);

            if (rootNode.has("data") && rootNode.get("data").isArray()) {
                JsonNode dataArray = rootNode.get("data");

                CountryPopulationData[] cities = objectMapper.treeToValue(dataArray, CountryPopulationData[].class);

                List<CountryPopulationData> sortedCities = new ArrayList<>(Arrays.asList(cities));

                sortedCities.sort(new CityComparator());

                List<CountryPopulationData> collatedCountryPopulationData = sortedCities.stream().limit(N).collect(Collectors.toList());

                return new ApiResponse<>("Most populated cities loaded successfully", collatedCountryPopulationData, false);
            } else {
                throw new JSONException ("Invalid API response format");

            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new JSONException ("Invalid API response format");
        }
    }
}

