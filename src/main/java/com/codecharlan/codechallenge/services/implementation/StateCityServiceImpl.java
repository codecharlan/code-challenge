package com.codecharlan.codechallenge.services.implementation;
import com.codecharlan.codechallenge.services.StateCityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
        import org.json.JSONObject;
        import org.springframework.http.ResponseEntity;
        import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
        import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Slf4j
public class StateCityServiceImpl implements StateCityService {
    String parsedCountry;
    @Override
    public JSONObject getCitiesAndState(String country) throws JsonProcessingException {
        JSONObject jsonObject = new JSONObject(country);
        parsedCountry = jsonObject.getString("country");

        String citiesEndpoint = "https://countriesnow.space/api/v0.1/countries";

        try {
            RestTemplate restTemplate = new RestTemplate();

            JSONObject citiesRequestBody = new JSONObject();
            citiesRequestBody.put("country", parsedCountry);
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

                        JSONArray statesArray = fetchStatesForCities(restTemplate, citiesArray);
                        response.put("states", statesArray);

                        System.out.println(response);
                        return response;
                    }
                }

                JSONObject response = new JSONObject();
                response.put("country", parsedCountry);
                response.put("cities", new JSONArray());
                return response;
            } else {
                throw new RestClientException("Failed to retrieve data from the endpoints.");
            }
        } catch (HttpClientErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            ObjectMapper objectMapper = new ObjectMapper();
            Map errorResponse = objectMapper.readValue(responseBody, Map.class);

            String errorMessage = (String) errorResponse.get("msg");
            log.info("StateCityServiceImpl getErrorMessage - Same as postman error :: [{}]", errorMessage);
            throw new HttpClientErrorException(NOT_FOUND, errorMessage);
        }
    }

    private JSONArray fetchStatesForCities(RestTemplate restTemplate, JSONArray citiesArray) {
        String stateCitiesEndpoint = "https://countriesnow.space/api/v0.1/countries/state/cities/q";

        JSONArray statesArray = new JSONArray();

        for (int j = 0; j < citiesArray.length(); j++) {
            String city = citiesArray.getString(j);
            JSONObject stateCitiesRequestBody = new JSONObject();
            stateCitiesRequestBody.put("country", parsedCountry);
            stateCitiesRequestBody.put("state", city);
            ResponseEntity<String> stateCitiesResponseEntity = restTemplate.getForEntity(stateCitiesEndpoint + "?state=" + city + "&country=" + parsedCountry, String.class);


            if (stateCitiesResponseEntity.getStatusCode().is2xxSuccessful()) {
                String stateCitiesResponseString = stateCitiesResponseEntity.getBody();
                JSONObject stateCitiesData = new JSONObject(stateCitiesResponseString);
                JSONArray states = stateCitiesData.getJSONArray("data");
                statesArray.put(new JSONObject().put("name", city).put("data", states));
            } else {
                throw new RestClientException("Failed to retrieve data from the state cities endpoint for city: " + city);
            }
        }

        return statesArray;
    }
}
