package com.duykypaul.arn;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author KyLC
 */
public class SaveCut {
    public static final int DEFAULT_POPULATION_SIZE = 5000;
    public static final double MUTATION_RATE = 0.001;
    public static final double CROSSOVER_RATE = 0.95;
    public static final double WORST_RATE = 0.01;
    public static final int ELITISM_COUNT = 3;
    public static final int RUNNING_TIME_LIMIT = 200;
    public static final int GENERATION_LIMIT = 3000;
    public static final int GENERATION_SAME_LIMIT = 300;
    public static final int BLADE_THICKNESS = 5;
    public static final String COMMA = ",";
    public static final String DIVIDER = "|";

    public static void main(String[] args) {
        String inputContent = "5623,1009,1640,1640,13000,13000,13000,13000,13000,13000,|1250,1250,1250,1200,1200,1000,1000,1000,1000|5";
        String inputContent1 = "1313,910,1188,2185,2545,2545,2900,2900,3285,4329,4329,4329,8594,1025|1250,1250,1250,115,115,122,122,122,122,122,122,122,122,122,122|5";
        String inputContent2 = "1930,2000,1406|560,560,560|5";
        String inputContent4 = "11700,11700,11700,11700,11700,11700,11700,11700,11700,11700|2000,2000,2000,2000,2000,3000,3000,3000,3000,3000,3000,3000,3000,3000,3000|0";
        String inputContent3 = "11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700,11700|2000,2000,2000,2000,2000,2000,2000,2000,2000,2000,2000,2000,2000,2000,2000,2000,2000,2000,2000,2000,3000,3000,3000,3000,3000,3000,3000,3000,3000,3000,3000,3000,3000,3000,3000,3000,3000,3000,3000,3000,3000,3000,3000,3000,3000,3000,3000,3000,3000,3000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000,7000|0";

//        testCase1();
        testCase2();
//        testCase3();
//        testCase4();
//        testCase5();
    }

    public static void testCase1() {
        List<Material> listStack = new ArrayList<>();
        listStack.add(new Material(10, 3000));
        listStack.add(new Material(20, 2000));
        listStack.add(new Material(30, 5000));
        listStack.add(new Material(40, 5000));
        listStack.add(new Material(50, 7000));
        listStack.add(new Material(20, 3000));

        List<Material> listStock = new ArrayList<>();
        listStock.add(new Material(200, 11700));
        listStock.add(new Material(15, 8000));
        listStock.add(new Material(30, 7995));

        run(listStack, listStock);
    }

    public static void testCase2() {
        List<Material> listStack = new ArrayList<>();
        listStack.add(new Material(300, 1250));
        listStack.add(new Material(200, 1200));
        listStack.add(new Material(400, 1000));
        listStack.add(new Material(500, 5000));
        listStack.add(new Material(600, 7000));

        List<Material> listStock = new ArrayList<>();
        listStock.add(new Material(1200, 11700));
        listStock.add(new Material(300, 5623));
        listStock.add(new Material(100, 1009));
        listStock.add(new Material(200, 1640));
        run(listStack, listStock);
    }

    public static void testCase3() {
        List<Material> listStack = new ArrayList<>();
        listStack.add(new Material(2000, 2000));
        listStack.add(new Material(1000, 3000));

        List<Material> listStock = new ArrayList<>();
        listStock.add(new Material(1000, 11700));

        run(listStack, listStock);
    }

    public static void testCase4() {
        List<Material> listStack = new ArrayList<>();
        listStack.add(new Material(200, 2000));
        listStack.add(new Material(100, 3000));
        listStack.add(new Material(300, 5000));
        listStack.add(new Material(400, 7000));

        List<Material> listStock = new ArrayList<>();
        listStock.add(new Material(1000, 11700));

        run(listStack, listStock);
    }

    public static void testCase5() {
        //1313,910,1188,2185,2545,2545,2900,2900,3285,4329,4329,4329,8594,1025|1250,1250,1250,115,115,122,122,122,122,122,122,122,122,122,122|5
        List<Material> listStack = new ArrayList<>();
        listStack.add(new Material(2, 115));
        listStack.add(new Material(6, 122));

        List<Material> listStock = new ArrayList<>();
        listStock.add(new Material(1, 1313));
        listStock.add(new Material(1, 910));

        run(listStack, listStock);
    }

    public static void testCase6() {
        //1313,910,1188,2185,2545,2545,2900,2900,3285,4329,4329,4329,8594,1025|1250,1250,1250,115,115,122,122,122,122,122,122,122,122,122,122|5
        List<Material> listStack = new ArrayList<>();
        listStack.add(new Material(3, 1250));
        listStack.add(new Material(2, 115));
        listStack.add(new Material(10, 122));

        List<Material> listStock = new ArrayList<>();
        listStock.add(new Material(1, 1313));
        listStock.add(new Material(1, 910));
        listStock.add(new Material(1, 1188));
        listStock.add(new Material(1, 2185));
        listStock.add(new Material(2, 2545));
        listStock.add(new Material(2, 2900));
        listStock.add(new Material(1, 3285));
        listStock.add(new Material(3, 4329));
        listStock.add(new Material(1, 8594));
        listStock.add(new Material(1, 1025));

        run(listStack, listStock);
    }

    public static void run(List<Material> listStack, List<Material> listStock) {
        final List<Integer> orders = new ArrayList<>();
        listStack.forEach(item -> {
            orders.addAll(Collections.nCopies(item.getQuantity(), item.getLength()));
        });

        final List<Integer> stocks = new ArrayList<>();
        listStock.forEach(item -> {
            stocks.addAll(Collections.nCopies(item.getQuantity(), item.getLength()));
        });

        String str = stocks.stream().map(String::valueOf).collect(Collectors.joining(COMMA))
            .concat(DIVIDER)
            .concat(orders.stream().map(String::valueOf).collect(Collectors.joining(COMMA)))
            .concat(DIVIDER)
            .concat(String.valueOf(BLADE_THICKNESS));

        System.out.println("input: " + str);

        Instant start = Instant.now();

        // create GA Object
        GeneticAlgorithm ga = new GeneticAlgorithm(DEFAULT_POPULATION_SIZE, MUTATION_RATE, CROSSOVER_RATE, ELITISM_COUNT, WORST_RATE, RUNNING_TIME_LIMIT, GENERATION_SAME_LIMIT);

        // Initialize population
        Population population = ga.initPopulation(stocks, orders, BLADE_THICKNESS, GENERATION_LIMIT);

        Instant initEnd = Instant.now();
        System.out.println("INIT POPULATION TIME: " + Duration.between(start, initEnd));

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
            Instant startCrossover = Instant.now();
            population = ga.crossoverPopulation(population);
            System.out.println("ONE CROSSOVER TIME: " + Duration.between(startCrossover, Instant.now()));

            // Apply mutation
            Instant startMutate = Instant.now();
            population = ga.mutatePopulation(population);
            System.out.println("ONE MUTATE TIME: " + Duration.between(startMutate, Instant.now()));

            // Evaluate population
            ga.evalPopulation(population);

            // Increment the current generation
            generation++;
            Instant endGeneration = Instant.now();
            System.out.println("ONE GENERATION TIME: " + Duration.between(startGeneration, endGeneration));
            System.out.println();
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
