package com.codecharlan.codechallenge.dtos.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrencyDataDto {
    @JsonProperty(value = "name")
    private String name;
    @JsonProperty(value = "currency")
    private String currency;
    @JsonProperty(value = "iso2")
    private String iso2;
    @JsonProperty(value = "iso3")
    private String iso3;

    @JsonCreator
    public CurrencyDataDto(@JsonProperty("name") String name,
                           @JsonProperty("currency") String currency,
                           @JsonProperty("iso2") String iso2,
                           @JsonProperty("iso3") String iso3) {
        this.name = name;
        this.currency = currency;
        this.iso2 = iso2;
        this.iso3 = iso3;
    }

}

