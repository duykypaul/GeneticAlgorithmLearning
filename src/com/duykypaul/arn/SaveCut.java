package com.duykypaul.arn;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 *
 * @author KyLC
 */
public class SaveCut {
    private static final int DEFAULT_POPULATION_SIZE = 5000;
    private static final double MUTATION_RATE = 0.01;
    private static final double CROSSOVER_RATE = 0.95;
    private static final double WORST_RATE = 0.01;
    private static final int ELITISM_COUNT = 3;
    private static final int RUNNING_TIME_LIMIT = 180;
    private static final int GENERATION_LIMIT = 1;
    private static final int GENERATION_SAME_LIMIT = 1700;

    public static void main(String[] args) {
        String inputContent = "5623,1009,1640,1640,13000,13000,13000,13000,13000,13000,|1250,1250,1250,1200,1200,1000,1000,1000,1000|5";
        String inputContent1 = "1313,910,1188,2185,2545,2545,2900,2900,3285,4329,4329,4329,8594,1025|1250,1250,1250,115,115,122,122,122,122,122,122,122,122,122,122|5";
        String inputContent2 = "1930,2000,1406|560,560,560|5";
        List<String> parts = Arrays.asList(inputContent1.split("\\|").clone());

        final List<Integer> stocks = Arrays.stream(parts.get(0).split(",").clone()).map(Integer::parseInt).collect(Collectors.toList());
        final List<Integer> orders = Arrays.stream(parts.get(1).split(",").clone()).map(Integer::parseInt).collect(Collectors.toList());
        final int CUT_WIDTH = Integer.parseInt(parts.get(2));

        Instant start = Instant.now();

        // create GA Object
        GeneticAlgorithm ga = new GeneticAlgorithm(DEFAULT_POPULATION_SIZE, MUTATION_RATE, CROSSOVER_RATE, ELITISM_COUNT, WORST_RATE, RUNNING_TIME_LIMIT, GENERATION_SAME_LIMIT);

        // Initialize population
        Population population = ga.initPopulation(stocks, orders, CUT_WIDTH, GENERATION_LIMIT);

        if (population.getRealPopulationSize() == 0) {
            System.out.println("can't resolve");
            return;
        }
        System.out.println("Population.getRealPopulationSize: " + population.getRealPopulationSize());

        //Evaluate the whole population
        ga.evalPopulation(population);

        int generation = 1;

        SortedMap<Double, Integer> resultSet = new TreeMap<>();

        while (!ga.isTerminationConditionMet(population, start, resultSet)) {
            Instant startGeneration = Instant.now();
            outputReport(population);

            // Apply crossover
            population = ga.crossoverPopulation(population);

            // Apply mutation
            population = ga.mutatePopulation(population);

            // Evaluate population
            ga.evalPopulation(population);

            // Increment the current generation
            generation++;
            Instant endGeneration = Instant.now();
            System.out.println("ONE GENERATION TIME: " + Duration.between(startGeneration, endGeneration));
        }

        Instant end = Instant.now();
        System.out.println("RUNNING TIME: " + Duration.between(start, end));
        System.out.println();
        System.out.println("Found solution in " + generation + " generations");
        outputReport(population);
        System.out.println("Number of kings: " + resultSet.size());
        System.out.println();
    }

    /**
     * Print fittest individual from population
     *
     * @param population p
     */
    private static void outputReport(Population population) {
        Individual best = population.getFittest(0);
        System.out.println("Best solution: " + best.getChromosome().stream().map(String::valueOf).collect(Collectors.joining(",")));
        System.out.println("Best value remain: " + best.getFitness());
        System.out.println();
    }
}
