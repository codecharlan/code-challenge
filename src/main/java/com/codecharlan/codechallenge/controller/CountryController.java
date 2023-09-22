package com.codecharlan.codechallenge.controller;

import com.codecharlan.codechallenge.dtos.request.CurrencyRequestDto;
import com.codecharlan.codechallenge.dtos.response.ApiResponse;
import com.codecharlan.codechallenge.models.CountryPopulationData;
import com.codecharlan.codechallenge.models.CountryInfo;
import com.codecharlan.codechallenge.models.CurrencyConversion;
import com.codecharlan.codechallenge.services.CityService;
import com.codecharlan.codechallenge.services.CountryInfoService;
import com.codecharlan.codechallenge.services.CurrencyConversionService;
import com.codecharlan.codechallenge.services.StateCityService;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@AllArgsConstructor
@RestController
@RequestMapping("api/explore")
public class CountryController {
    private final CountryInfoService countryInfoService;
    private final CityService cityService;
    private final StateCityService stateCityService;
    private final CurrencyConversionService conversionService;

    @GetMapping("/cities")
    public ResponseEntity<ApiResponse<List<CountryPopulationData>>> getMostPopulatedCities(@RequestParam int N) {
        return new ResponseEntity<>(cityService.getMostPopulatedCities(N), OK);
    }


    @PostMapping("/country-info")
    public ResponseEntity<ApiResponse<CountryInfo>> getCountryInfo(@RequestBody CurrencyRequestDto requestDto) {
        return new ResponseEntity<>(countryInfoService.getCountryInfo(requestDto.getCountry()), OK);
    }

    @PostMapping("/states-and-cities")
    public ResponseEntity<JSONObject> getStatesAndCitiesInCountry(@RequestBody String country) {
        JSONObject combinedData = stateCityService.getCitiesAndState(country);
        return new ResponseEntity<>(combinedData, CREATED);
    }

    @PostMapping("/convert-currency")
    public ResponseEntity<ApiResponse<CurrencyConversion>> convertCurrency(
            @RequestBody CurrencyRequestDto requestDto, @RequestParam BigDecimal amount, @RequestParam String targetCurrency) {
        return new ResponseEntity<>(conversionService.convertCurrency(requestDto.getCountry(), amount, targetCurrency), CREATED);
    }
}



