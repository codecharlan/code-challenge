package com.codecharlan.codechallenge.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONObject;

public interface StateCityService {
    JSONObject getCitiesAndState(String country) throws JsonProcessingException;
}
