package com.codecharlan.codechallenge.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CountryInfo {
    @JsonProperty(value = "capital")
    private String capital;
    @JsonProperty(value = "currency")
    private String currency;
    @JsonProperty(value = "iso2")
    private String iso2;
    @JsonProperty(value = "iso3")
    private String iso3;
    @JsonProperty(value = "longitude")
    private int longitude;
    @JsonProperty(value = "latitude")
    private int latitude;
    @JsonProperty(value = "populationCounts")
    Map<Integer, Long> populationCounts;

}
