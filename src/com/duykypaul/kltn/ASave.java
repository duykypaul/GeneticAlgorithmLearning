package com.duykypaul.kltn;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ASave {
    private static final int DEFAULT_POPULATION_SIZE = 5000;
    private static final double MUTATION_RATE = 0.01;
    private static final double CROSSOVER_RATE = 0.95;
    private static final int ELITISM_COUNT = 3;
    private static final int RUNNING_TIME_LIMIT = 200;
    private static final int GENERATION_LIMIT = 1;

    public static void main(String[] args) {
        List<Stack> listStack = new ArrayList<>();
        listStack.add(new Stack(10, 3000, LocalDate.parse("2021-02-28")));
        listStack.add(new Stack(20, 2000, LocalDate.parse("2021-02-28")));
        listStack.add(new Stack(30, 5000, LocalDate.parse("2021-02-28")));

        listStack.add(new Stack(40, 5000, LocalDate.parse("2021-03-05")));
        listStack.add(new Stack(50, 7000, LocalDate.parse("2021-03-05")));
        listStack.add(new Stack(20, 3000, LocalDate.parse("2021-03-05")));

        List<Stock> listStock = new ArrayList<>();
        listStock.add(new Stock(200, 13000, LocalDate.parse("2021-02-12")));
        listStock.add(new Stock(15, 8000, LocalDate.parse("2021-02-15")));
        listStock.add(new Stock(30, 7995, LocalDate.parse("2021-02-15")));

        final List<Integer> orders = new ArrayList<>();
        final List<LocalDate> ordersDate = new ArrayList<>();
        listStack.forEach(item -> {
            orders.addAll(Collections.nCopies(item.getQuantity(), item.getLength()));
            ordersDate.addAll(Collections.nCopies(item.getQuantity(), item.getDeliveryDate()));
        });

        final List<Integer> stocks = new ArrayList<>();
        final List<LocalDate> stocksDate = new ArrayList<>();
        listStock.forEach(item -> {
            stocks.addAll(Collections.nCopies(item.getQuantity(), item.getLength()));
            stocksDate.addAll(Collections.nCopies(item.getQuantity(), item.getImportDate()));
        });

        final List<Machine> machines = new ArrayList<>();
        machines.add(new Machine(5, 1, LocalDate.parse("2021-02-20"), 240));
        machines.add(new Machine(5, 1, LocalDate.parse("2021-02-20"), 240));
        machines.add(new Machine(5, 1, LocalDate.parse("2021-02-20"), 240));

        Instant start = Instant.now();

        // create GA Object
        GeneticAlgorithm ga = new GeneticAlgorithm(DEFAULT_POPULATION_SIZE, MUTATION_RATE,
            CROSSOVER_RATE, ELITISM_COUNT, RUNNING_TIME_LIMIT);

        // Initialize population
        Population population = ga.initPopulation(stocks, stocksDate, orders, ordersDate, machines, GENERATION_LIMIT);

        if (Population.ARNsN == 0) {
            System.out.println("can't resolve");
            return;
        }
        System.out.println("Population.ARNsN: " + Population.ARNsN);
        ga.evalPopulation(population);
        int generation = 1;

        TreeMap<Double, Integer> resultSet = new TreeMap<>();

        while (!ga.isTerminationConditionMet(population, start, resultSet)) {
            outputReport(population);

            // Apply crossover
            //population = ga.crossoverPopulation(population);

            // Apply mutation
            population = ga.mutatePopulation(population);

            // Evaluate population
            ga.evalPopulation(population);

            // Increment the current generation
            generation++;
        }

        System.out.println("Found solution in " + generation + " generations");
        outputReport(population);
        System.out.println("Number of kings: " + resultSet.size());
        Instant end = Instant.now();
        System.out.println(Duration.between(start, end));
    }

    /**
     * Print fittest individual from population
     *
     * @param population
     */
    private static void outputReport(Population population) {
        Individual best = population.getFittest(0);
        System.out.println("Best solution stocks index: " + best.getChromosome().stream().map(String::valueOf).collect(Collectors.joining(",")));
        System.out.println("Best solution machines index: " + best.getChromosomeMachine().stream().map(String::valueOf).collect(Collectors.joining(",")));
        System.out.println("Best solution datetime cut product: " + best.getChromosomeTime().stream().map(String::valueOf).collect(Collectors.joining(",")));
        System.out.println("Best value remain: " + best.getFitness());
        System.out.println();
    }
}
