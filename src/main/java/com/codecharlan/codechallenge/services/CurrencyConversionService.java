package com.codecharlan.codechallenge.services;

import com.codecharlan.codechallenge.dtos.response.ApiResponse;
import com.codecharlan.codechallenge.models.CurrencyConversion;

import java.math.BigDecimal;
public interface CurrencyConversionService {
    ApiResponse<CurrencyConversion> convertCurrency(String country, BigDecimal amount, String targetCurrency);
}

