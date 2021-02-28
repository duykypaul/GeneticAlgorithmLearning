package com.duykypaul.kltn;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

public class ARNV3 {
    private static final int DEFAULT_POPULATION_SIZE = 5000;
    private static final double MUTATION_RATE =  0.01;
    private static final double CROSSOVER_RATE = 0.95;
    private static final int ELITISM_COUNT =  3;
    private static final int RUNNING_TIME_LIMIT = 3;
    private static final int GENERATION_LIMIT = 1;

    private static final String COMMA = ",";

    public static void main(String[] args) {
        List<Stack> listStack = new ArrayList<>();
        listStack.add(new Stack(2, 3000, LocalDate.parse("2021-02-28")));
        listStack.add(new Stack(3, 2000, LocalDate.parse("2021-02-28")));

        listStack.add(new Stack(3, 5000, LocalDate.parse("2021-03-05")));
        listStack.add(new Stack(4, 7000, LocalDate.parse("2021-03-05")));

        List<Stock> listStock = new ArrayList<>();
        listStock.add(new Stock(20, 13000, LocalDate.parse("2021-02-12")));
        listStock.add(new Stock(15, 8000, LocalDate.parse("2021-02-15")));

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
        machines.add(new Machine(5,1, LocalDate.parse("2021-02-20"), 240));
        machines.add(new Machine(5,1, LocalDate.parse("2021-02-20"), 240));
        machines.add(new Machine(5,1, LocalDate.parse("2021-02-20"), 240));

        Instant start = Instant.now();

        // create GA Object
        GeneticAlgorithm ga = new GeneticAlgorithm(DEFAULT_POPULATION_SIZE,  MUTATION_RATE,
            CROSSOVER_RATE, ELITISM_COUNT, RUNNING_TIME_LIMIT);

        // Initialize population
        Population population = ga.initPopulation(stocks, stocksDate, orders, ordersDate, machines, GENERATION_LIMIT);

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
            //population = ga.crossoverPopulation(population);

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
