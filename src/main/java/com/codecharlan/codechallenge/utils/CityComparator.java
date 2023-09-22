package com.codecharlan.codechallenge.utils;

import com.codecharlan.codechallenge.models.CountryPopulationData;
import com.codecharlan.codechallenge.models.PopulationCount;

import java.util.Comparator;

public class CityComparator implements Comparator<CountryPopulationData> {
    @Override
    public int compare(CountryPopulationData city1, CountryPopulationData city2) {
        Double maxPopulation1 = getMaxPopulation(city1);
        Double maxPopulation2 = getMaxPopulation(city2);

        return maxPopulation2.compareTo(maxPopulation1);
    }

    private Double getMaxPopulation(CountryPopulationData city) {
        if (city != null && city.getPopulationCounts() != null) {
            return city.getPopulationCounts().stream()
                    .map(PopulationCount::getValue)
                    .filter(this::isValidNumber)
                    .map(Double::parseDouble)
                    .max(Double::compareTo)
                    .orElse(0.0);
        }
        return 0.0;
    }

    private boolean isValidNumber(String str) {
        return str != null && !str.trim().isEmpty() && str.matches("-?\\d+(\\.\\d+)?");
    }
}
