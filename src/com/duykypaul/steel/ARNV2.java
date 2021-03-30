package com.duykypaul.steel;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ARNV2 {
    private static final int DEFAULT_POPULATION_SIZE = 30000000;
    private static final double MUTATION_RATE =  0.01;
    private static final double CROSSOVER_RATE = 0.95;
    private static final int ELITISM_COUNT =  3;
    private static final int RUNNING_TIME_LIMIT = 30;
    private static final int GENERATION_LIMIT = 180;
    public static void main(String[] args) {

//        String inputContent = "5623,1009,1640,1640,13000,13000,13000,13000,13000|1250,1250,1250,1200,1200,1000,1000,1000,1000|5";
//        String inputContent = "1313,910,1188,2185,2545,2545,2900,2900,3285,4329,4329,4329,8594,1025|1250,1250,1250,115,115,122,122,122,122,122,122,122,122,122,122|5";
        String inputContent = "1313,910,1188,2185,2545,2545,2900,2900,3285,4329,4329,4329,8594,1025|1250,1250,1250,115,115,122,122,122,122,122,122,122,122,122,122|5";

        List<String> parts = Arrays.asList(inputContent.split("\\|").clone());

        final List<Integer> stocks = Arrays.stream(parts.get(0).split(",").clone()).map(Integer::parseInt).collect(Collectors.toList());
        final List<Integer> orders = Arrays.stream(parts.get(1).split(",").clone()).map(Integer::parseInt).collect(Collectors.toList());
        final int CUT_WIDTH = Integer.parseInt(parts.get(2));
        Instant start = Instant.now();

        // create GA Object
        GeneticAlgorithm ga = new GeneticAlgorithm(DEFAULT_POPULATION_SIZE,  MUTATION_RATE, CROSSOVER_RATE, ELITISM_COUNT, RUNNING_TIME_LIMIT);

        // Initialize population
        Population population = ga.initPopulation(stocks, orders, CUT_WIDTH, GENERATION_LIMIT);

        if(Population.ARNsN == 0) {
            System.out.println("can't resolve");
            return;
        }
        System.out.println("Population.ARNsN: " + Population.ARNsN);
        ga.evalPopulation(population);
        int generation = 1;

        Set<String> stringSet = new HashSet<>();

        while (!ga.isTerminationConditionMet(population, start)) {
            // Print fittest individual from population
            stringSet.add(population.getFittest(0).toString());
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
        stringSet.add(population.getFittest(0).toString());
        System.out.println("set size: " + stringSet.size());

        Instant end = Instant.now();
        System.out.println(Duration.between(start, end));
    }
}
