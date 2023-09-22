package com.codecharlan.codechallenge.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
@ToString
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryPopulationData {
    @JsonProperty("city")
    private String city;
    @JsonProperty("country")
    private String country;
    @JsonProperty("populationCounts")
    private List<PopulationCount> populationCounts;
}
