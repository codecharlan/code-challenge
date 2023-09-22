package com.codecharlan.codechallenge.services;

import com.codecharlan.codechallenge.dtos.response.ApiResponse;
import com.codecharlan.codechallenge.models.CountryInfo;

public interface CountryInfoService {
    ApiResponse<CountryInfo> getCountryInfo(String country);
}

