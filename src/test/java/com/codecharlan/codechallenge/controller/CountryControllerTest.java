package com.codecharlan.codechallenge.controller;

import com.codecharlan.codechallenge.dtos.request.CurrencyRequestDto;
import com.codecharlan.codechallenge.dtos.response.ApiResponse;
import com.codecharlan.codechallenge.models.CountryInfo;
import com.codecharlan.codechallenge.models.CountryPopulationData;
import com.codecharlan.codechallenge.models.CurrencyConversion;
import com.codecharlan.codechallenge.services.CityService;
import com.codecharlan.codechallenge.services.CountryInfoService;
import com.codecharlan.codechallenge.services.CurrencyConversionService;
import com.codecharlan.codechallenge.services.StateCityService;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CountryControllerTest {

    private CountryController countryController;
    private CountryInfoService countryInfoService;
    private CityService cityService;
    private StateCityService stateCityService;
    private CurrencyConversionService conversionService;

    @BeforeEach
    void setUp() {
        countryInfoService = mock(CountryInfoService.class);
        cityService = mock(CityService.class);
        stateCityService = mock(StateCityService.class);
        conversionService = mock(CurrencyConversionService.class);

        countryController = new CountryController(
                countryInfoService,
                cityService,
                stateCityService,
                conversionService
        );
    }

    @Test
    void testGetMostPopulatedCities() {
        List<CountryPopulationData> populationDataList = new ArrayList<>();
        when(cityService.getMostPopulatedCities(5)).thenReturn(new ApiResponse<>("Most populated cities loaded successfully",populationDataList, false));
        ResponseEntity<ApiResponse<List<CountryPopulationData>>> response = countryController.getMostPopulatedCities(5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetCountryInfo() {
        CountryInfo countryInfo = new CountryInfo();

        when(countryInfoService.getCountryInfo("nigeria")).thenReturn(new ApiResponse<>("Country information loaded successfully", countryInfo, false));
        ResponseEntity<ApiResponse<CountryInfo>> response = countryController.getCountryInfo(new CurrencyRequestDto("nigeria"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetStatesAndCitiesInCountry() {
        JSONObject combinedData = new JSONObject();

        when(stateCityService.getCitiesAndState("CountryName")).thenReturn(combinedData);
        ResponseEntity<JSONObject> response = countryController.getStatesAndCitiesInCountry("CountryName");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testConvertCurrency() {
        CurrencyConversion currencyConversion = new CurrencyConversion();

        when(conversionService.convertCurrency("CountryName", BigDecimal.TEN, "TargetCurrency")).thenReturn(new ApiResponse<>("Data retrieved Successfully",currencyConversion, false));
        ResponseEntity<ApiResponse<CurrencyConversion>> response = countryController.convertCurrency(new CurrencyRequestDto("CountryName"), BigDecimal.TEN, "TargetCurrency");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
}
