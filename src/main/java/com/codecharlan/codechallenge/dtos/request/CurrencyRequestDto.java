package com.codecharlan.codechallenge.dtos.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@ToString
@Getter
@Setter
public class CurrencyRequestDto {
    private String country;
    @JsonCreator
    public CurrencyRequestDto(@JsonProperty("country") String country) {
        this.country = country;
    }
}
