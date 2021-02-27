package com.duykypaul.kltn;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ARNV3 {
    private static final int DEFAULT_POPULATION_SIZE = 5000;
    private static final double MUTATION_RATE =  0.01;
    private static final double CROSSOVER_RATE = 0.95;
    private static final int ELITISM_COUNT =  3;
    private static final int RUNNING_TIME_LIMIT = 3;
    private static final int GENERATION_LIMIT = 1;

    private static final String COMMA = ",";

    public static void main(String[] args) {
        List<Stack> listStack1 = new ArrayList<>();
        listStack1.add(new Stack(3, 5000, LocalDate.parse("2021-02-28")));
        listStack1.add(new Stack(1, 10000, LocalDate.parse("2021-02-28")));

        String inputStocks = "7000,5000,13000,13000,6000";
        String inputStocksDate = "2021-02-12,2021-02-12,2021-02-15,2021-02-15,2021-02-15";

        final List<Integer> stocks = Arrays.stream(inputStocks.split(COMMA)).map(Integer::parseInt).collect(Collectors.toList());
        final List<LocalDate> stocksDate = Arrays.stream(inputStocksDate.split(COMMA)).map(LocalDate::parse).collect(Collectors.toList());

        final List<Integer> orders = new ArrayList<>();
        final List<LocalDate> ordersDate = new ArrayList<>();
        listStack1.forEach(item -> {
            orders.addAll(Collections.nCopies(item.getQuantity(), item.getLength()));
            ordersDate.addAll(Collections.nCopies(item.getQuantity(), item.getDeliveryDate()));
        });

        final List<Machine> machines = new ArrayList<>();
        machines.add(new Machine(6,1, LocalDate.parse("2021-02-20"), 240));
        machines.add(new Machine(6,1, LocalDate.parse("2021-02-20"), 240));
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
