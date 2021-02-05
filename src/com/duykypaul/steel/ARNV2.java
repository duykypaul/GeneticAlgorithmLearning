package com.duykypaul.steel;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ARNV2 {
    public static void main(String[] args) {
//        String inputContent = "7000,5000,13000,13000,6000|5000,5000,5000,10000|5";
        String inputContent = "2220,2220,2534,7093,7093,13000,13000,13000,13000,13000,13000,13000,13000,13000,13000,13000,13000,13000,13000,13000|3303,3303,3303,3303,3303,3303,31803180,3180,4080,4080,4080,4080,4180,4180,990|5";
//        String inputContent = "2220,2220,2534,7093,7093,13000,13000,13000,13000,13000,13000|3303,3303,3303,3303,3303,3303,3180,3180,3180,3180,3180,3180,4080,4080,4080,4080,4180,4180,990,990,990,990,990,990,2385,2385,2385,2385,2185,2185|5";

        List<String> parts = Arrays.asList(inputContent.split("\\|").clone());

        final List<Integer> stocks = Arrays.stream(parts.get(0).split(",").clone()).map(Integer::parseInt).collect(Collectors.toList());
        final List<Integer> orders = Arrays.stream(parts.get(1).split(",").clone()).map(Integer::parseInt).collect(Collectors.toList());
        final int CUT_WIDTH = Integer.parseInt(parts.get(2));

        Instant start = Instant.now();
        // create GA Object
        GeneticAlgorithm ga = new GeneticAlgorithm(5000, 0.01, 0.95, 10);

        // Initialize population
        Population population = ga.initPopulation(stocks, orders, CUT_WIDTH);
        System.out.println("time init population: " + Duration.between(start, Instant.now()).getSeconds());

        if(Population.ARNsN == 0) {
            System.out.println("can't resolve");
            return;
        }
        ga.evalPopulation(population);
        int generation = 1;
        while (!ga.isTerminationConditionMet(population) && Duration.between(start, Instant.now()).getSeconds() <= 1) {
            // Print fittest individual from population
            System.out.println("Best solution: " + population.getFittest(0).toString());
            System.out.println("Best value: " + population.getFittest(0).getFitness());
            System.out.println();

            // Apply crossover
//            population = ga.crossoverPopulation(population);

            // Apply mutation
            population = ga.mutatePopulation(population);

            // Evaluate population
            ga.evalPopulation(population);

            // Increment the current generation
            generation++;
        }

        System.out.println("Found solution in " + generation + " generations");
        System.out.println("Best solution final: " + population.getFittest(0).toString());
        System.out.println("Best value final: " + population.getFittest(0).getFitness());

        Instant end = Instant.now();
        System.out.println(Duration.between(start, end));
    }
}
