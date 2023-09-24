package com.codecharlan.codechallenge.utils;

import com.codecharlan.codechallenge.models.CountryPopulationData;
import com.codecharlan.codechallenge.models.PopulationCount;

import java.util.Comparator;
import java.util.Optional;

public class CityComparator implements Comparator<CountryPopulationData> {
    @Override
    public int compare(CountryPopulationData city1, CountryPopulationData city2) {
        if (city1 == null || city2 == null) {
            return 0;
        }

        double population1 = getPopulation(city1);
        double population2 = getPopulation(city2);

        if (isInvalidPopulationValue(population1) && isInvalidPopulationValue(population2)) {
            return 0;
        } else if (isInvalidPopulationValue(population1)) {
            return 1;
        } else if (isInvalidPopulationValue(population2)) {
            return -1;
        }

        return Double.compare(population2, population1);
    }

    private double getPopulation(CountryPopulationData city) {
        if (city != null && city.getPopulationCounts() != null) {
            Optional<Double> maxPopulation = city.getPopulationCounts()
                    .stream()
                    .map(PopulationCount::getValue)
                    .filter(this::isValidNumber)
                    .map(Double::parseDouble)
                    .max(Double::compareTo);

            return maxPopulation.orElse(0.0);
        }
        return 0.0;
    }

    private boolean isInvalidPopulationValue(double population) {
        return Double.compare(population, 0.0) == 0;
    }

    private boolean isValidNumber(String str) {
        return str != null && !str.trim().isEmpty() && str.matches("-?\\d+(\\.\\d+)?");
    }
}
