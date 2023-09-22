package com.codecharlan.codechallenge.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PopulationCount {
    @JsonProperty(value = "year")
    private String year;
    @JsonProperty(value = "value")
    private String value;
    @JsonProperty(value = "sex")
    private String sex;
    @JsonProperty(value = "reliabilty")
    private String reliabilty;
}

