package com.codecharlan.codechallenge.services.implementation;

import com.codecharlan.codechallenge.services.StateCityService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class StateCityServiceImpl implements StateCityService {
    String citiesEndpoint = "https://countriesnow.space/api/v0.1/countries/cities";
    String stateCitiesEndpoint = "https://countriesnow.space/api/v0.1/countries/state/cities";

    @Override
    public JSONObject getCitiesAndState(String country) {

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            JSONObject citiesRequestBody = new JSONObject();
            citiesRequestBody.put("country", country);
            HttpEntity<String> citiesRequestEntity = new HttpEntity<>(citiesRequestBody.toString(), headers);
            ResponseEntity<String> citiesResponseEntity = restTemplate.exchange(citiesEndpoint, HttpMethod.POST, citiesRequestEntity, String.class);

            if (citiesResponseEntity.getStatusCodeValue() == 200) {
                String citiesResponseString = citiesResponseEntity.getBody();
                JSONObject citiesData = new JSONObject(citiesResponseString);

                JSONObject stateCitiesRequestBody = new JSONObject();
                stateCitiesRequestBody.put("country", country);
                HttpEntity<String> stateCitiesRequestEntity = new HttpEntity<>(stateCitiesRequestBody.toString(), headers);
                ResponseEntity<String> stateCitiesResponseEntity = restTemplate.exchange(stateCitiesEndpoint, HttpMethod.POST, stateCitiesRequestEntity, String.class);

                if (stateCitiesResponseEntity.getStatusCodeValue() == 200) {
                    String stateCitiesResponseString = stateCitiesResponseEntity.getBody();
                    JSONObject stateCitiesData = new JSONObject(stateCitiesResponseString);

                    JSONArray states = stateCitiesData.getJSONArray("data");

                    JSONObject response = new JSONObject();
                    response.put("country", country);
                    response.put("states", states);

                    return response;
                }
            } else {
                throw new RestClientException("Failed to retrieve data from the endpoints.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error processing response");
        }
        throw new JSONException("Invalid API response format");
    }
}
