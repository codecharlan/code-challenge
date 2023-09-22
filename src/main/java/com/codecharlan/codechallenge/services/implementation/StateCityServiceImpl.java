package com.codecharlan.codechallenge.services.implementation;
import com.codecharlan.codechallenge.services.StateCityService;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
public class StateCityServiceImpl implements StateCityService {
    String citiesEndpoint = "https://countriesnow.space/api/v0.1/countries/cities";
    String stateCitiesEndpoint = "https://countriesnow.space/api/v0.1/countries/state/cities";
    @Override
    public JSONObject getCitiesAndState(String country) {

        try {
            HttpClient httpClient = HttpClients.createDefault();

            HttpPost citiesRequest = new HttpPost(citiesEndpoint);
            JSONObject citiesRequestBody = new JSONObject();
            citiesRequestBody.put("country", country);
            citiesRequest.setEntity(new StringEntity(citiesRequestBody.toString()));
            citiesRequest.setHeader("Content-Type", "application/json");

            HttpResponse citiesResponse = httpClient.execute(citiesRequest);
            if (citiesResponse.getStatusLine().getStatusCode() == 200) {
                String citiesResponseString = EntityUtils.toString(citiesResponse.getEntity());
                JSONObject citiesData = new JSONObject(citiesResponseString);

                HttpPost stateCitiesRequest = new HttpPost(stateCitiesEndpoint);
                JSONObject stateCitiesRequestBody = new JSONObject();
                stateCitiesRequestBody.put("country", country);
                stateCitiesRequest.setEntity(new StringEntity(stateCitiesRequestBody.toString()));
                stateCitiesRequest.setHeader("Content-Type", "application/json");

                HttpResponse stateCitiesResponse = httpClient.execute(stateCitiesRequest);

                if (stateCitiesResponse.getStatusLine().getStatusCode() == 200) {
                    String stateCitiesResponseString = EntityUtils.toString(stateCitiesResponse.getEntity());
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


