package com.codecharlan.codechallenge.services.implementation;

import com.codecharlan.codechallenge.services.StateCityService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class StateCityServiceImpl implements StateCityService {

    private final RestTemplate restTemplate;

    @Value("${base.api.url}")
    private String baseApiUrl;

    @Override
    public JSONObject getCitiesAndState(String country) {
        String parsedCountry = parseCountry(country);

        String citiesEndpoint = baseApiUrl + "/countries";

        try {
            ResponseEntity<String> citiesResponseEntity = restTemplate.getForEntity(citiesEndpoint, String.class);

            if (citiesResponseEntity.getStatusCode().is2xxSuccessful()) {
                String citiesResponseString = citiesResponseEntity.getBody();
                JSONObject citiesData = new JSONObject(citiesResponseString);

                JSONArray dataArray = citiesData.getJSONArray("data");
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject countryData = dataArray.getJSONObject(i);
                    String countryName = countryData.getString("country");

                    if (parsedCountry.equals(countryName)) {
                        JSONArray citiesArray = countryData.getJSONArray("cities");

                        JSONObject response = new JSONObject();
                        response.put("country", parsedCountry);
                        response.put("cities", citiesArray);

                        JSONArray statesArray = fetchStatesForCities(parsedCountry, citiesArray);
                        response.put("states", statesArray);

                        log.info("StateCityServiceImpl getStateAndCitiesResponse: {}", response);
                        return response;
                    }
                }

                JSONObject response = new JSONObject();
                response.put("country", parsedCountry);
                response.put("cities", new JSONArray());
                return response;
            } else {
                throw new RuntimeException("Failed to retrieve data from the endpoints.");
            }
        } catch (Exception e) {
            handleException(e);
        }

        return null;
    }

    private String parseCountry(String country) {
        JSONObject jsonObject = new JSONObject(country);
        return jsonObject.getString("country");
    }

    private JSONArray fetchStatesForCities(String country, JSONArray citiesArray) {
        String stateCitiesEndpoint = baseApiUrl + "/countries/state/cities/q";

        JSONArray statesArray = new JSONArray();

        for (int j = 0; j < citiesArray.length(); j++) {
            String city = citiesArray.getString(j);
            ResponseEntity<String> stateCitiesResponseEntity = restTemplate.getForEntity(
                    stateCitiesEndpoint + "?state=" + city + "&country=" + country,
                    String.class);

            if (stateCitiesResponseEntity.getStatusCode().is2xxSuccessful()) {
                String stateCitiesResponseString = stateCitiesResponseEntity.getBody();
                JSONObject stateCitiesData = new JSONObject(stateCitiesResponseString);
                JSONArray states = stateCitiesData.getJSONArray("data");
                statesArray.put(new JSONObject().put("name", city).put("data", states));
            } else {
                throw new RuntimeException("Failed to retrieve data from the state cities endpoint for city: " + city);
            }
        }

        return statesArray;
    }

    private void handleException(Exception e) {
        log.error("An error occurred: {}", e.getMessage());

        if (e instanceof HttpClientErrorException httpException) {
            String responseBody = httpException.getResponseBodyAsString();
            JSONObject errorResponse = new JSONObject(responseBody);
            String errorMessage = errorResponse.getString("msg");
            log.info("StateCityServiceImpl getErrorMessage - Same as postman error: [{}]", errorMessage);
            throw new HttpClientErrorException(NOT_FOUND, errorMessage);
        }
    }
}
