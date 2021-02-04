package com.duykypaul.chapter4;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TSP {
    private static final int TIMEOUT = 20;
    public static int maxGenerations = 3000;
    public static int numCities = 100;
    public static City[] cities = new City[numCities];

    static {
        // Loop to create random cities
        for (int cityIndex = 0; cityIndex < numCities; cityIndex++) {
            int xPos = (int) (100 * Math.random());
            int yPos = (int) (100 * Math.random());
            cities[cityIndex] = new City(xPos, yPos);
        }
    }

    public static void main(String[] args) {
        Instant start = Instant.now();
        List<Route> routeList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            routeList.add(getTop());
        }
        routeList.forEach(nb -> System.out.println(nb.getDistance() + " "));

        Instant end = Instant.now();
        System.out.println("Duration: " + Duration.between(start, end));

        /*Instant start = Instant.now();
        // Initial GA
        GeneticAlgorithm ga = new GeneticAlgorithm(100, 0.001, 0.9, 2, 5);

        // Initialize population
        Population population = ga.initPopulation(cities.length);
        // TODO: Evaluate population
        ga.evalPopulation(population, cities);
        // Keep track of current generation
        int generation = 1;
        // Start evolution loop
        while (!ga.isTerminationConditionMet(generation, maxGenerations, start, TIMEOUT)) {
            // TODO: Print fittest individual from population
            Individual fittest = population.getFittest(0);
            Route route = new Route(fittest, cities);
            System.out.println("G" + generation + " Best distance: " + route.getDistance());
            System.out.println();

            // TODO: Apply crossover
            population = ga.crossoverPopulation(population);

            // TODO: Apply mutation
            population = ga.mutatePopulation(population);

            // TODO: Evaluate population
            ga.evalPopulation(population, cities);
            // Increment the current generation
            generation++;
        }
        // TODO: Display results
        System.out.println("Stopped after " + maxGenerations + " generations.");
        Individual fittest = population.getFittest(0);
        Route route = new Route(fittest, cities);
        System.out.println(" Best distance: " + route.getDistance());
        Instant end = Instant.now();
        System.out.println("Duration: " + Duration.between(start, end));*/

    }

    public static Route getTop(){
        Instant start = Instant.now();
        // Initial GA
        GeneticAlgorithm ga = new GeneticAlgorithm(100, 0.001, 0.9, 2, 5);

        // Initialize population
        Population population = ga.initPopulation(cities.length);
        // TODO: Evaluate population
        ga.evalPopulation(population, cities);
        // Keep track of current generation
        int generation = 1;
        // Start evolution loop
        while (!ga.isTerminationConditionMet(generation, maxGenerations, start, TIMEOUT)) {
            // TODO: Print fittest individual from population
            Individual fittest = population.getFittest(0);

            // TODO: Apply crossover
            population = ga.crossoverPopulation(population);

            // TODO: Apply mutation
            population = ga.mutatePopulation(population);

            // TODO: Evaluate population
            ga.evalPopulation(population, cities);
            // Increment the current generation
            generation++;
        }
        // TODO: Display results
        Individual fittest = population.getFittest(0);
        return new Route(fittest, cities);
    }

}
