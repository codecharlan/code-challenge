package com.codecharlan.codechallenge.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.math.BigDecimal;
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrencyConversion {
    private String sourceCurrency;
    private String targetCurrency;
    private BigDecimal amount;
    private BigDecimal convertedAmount;
}
