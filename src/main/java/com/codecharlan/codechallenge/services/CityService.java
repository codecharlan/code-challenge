package com.codecharlan.codechallenge.services;

import com.codecharlan.codechallenge.dtos.response.ApiResponse;
import com.codecharlan.codechallenge.models.CountryPopulationData;

import java.util.List;
public interface CityService {
    ApiResponse<List<CountryPopulationData>> getMostPopulatedCities(int N);
}

