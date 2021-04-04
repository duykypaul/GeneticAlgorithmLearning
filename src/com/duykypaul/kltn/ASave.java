package com.duykypaul.kltn;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ASave {
    private static final int DEFAULT_POPULATION_SIZE = 5000;
    private static final double MUTATION_RATE = 0.01;
    private static final double CROSSOVER_RATE = 0.95;
    private static final double WORST_RATE = 0.01;
    private static final int ELITISM_COUNT = 3;
    private static final int RUNNING_TIME_LIMIT = 300;
    private static final int GENERATION_LIMIT = 1;

    public static void main(String[] args) {
        List<Stack> listStack = new ArrayList<>();
        listStack.add(new Stack(1, 1, 10, 3000, LocalDate.parse("2021-02-28")));
        listStack.add(new Stack(1, 2, 20, 2000, LocalDate.parse("2021-02-28")));
        listStack.add(new Stack(1, 3, 30, 5000, LocalDate.parse("2021-02-28")));

        listStack.add(new Stack(2, 1, 40, 5000, LocalDate.parse("2021-03-05")));
        listStack.add(new Stack(2, 2, 50, 7000, LocalDate.parse("2021-03-05")));
        listStack.add(new Stack(2, 3, 20, 3000, LocalDate.parse("2021-03-05")));

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
        GeneticAlgorithm ga = new GeneticAlgorithm(DEFAULT_POPULATION_SIZE, MUTATION_RATE, CROSSOVER_RATE, ELITISM_COUNT, WORST_RATE, RUNNING_TIME_LIMIT);

        // Initialize population
        Population population = ga.initPopulation(stocks, stocksDate, orders, ordersDate, machines, GENERATION_LIMIT);

        if (Population.ARNsN == 0) {
            System.out.println("can't resolve");
            return;
        }
        System.out.println("Population.ARNsN: " + Population.ARNsN);

        //Evaluate the whole population
        ga.evalPopulation(population);

        int generation = 1;

        SortedMap<Double, Integer> resultSet = new TreeMap<>();

        while (!ga.isTerminationConditionMet(population, start, resultSet)) {
            outputReport(population);

            // Apply crossover
            population = ga.crossoverPopulation(population);

            // Apply mutation
            population = ga.mutatePopulation(population);

            // Evaluate population
            ga.evalPopulation(population);

            // Increment the current generation
            generation++;
        }

        Instant end = Instant.now();
        System.out.println(Duration.between(start, end));
        System.out.println();
        System.out.println("Found solution in " + generation + " generations");
        outputReport(population);
        System.out.println("Number of kings: " + resultSet.size());
        System.out.println();

        outputStatistic(population, machines, listStack);

    }

    /**
     * Print fittest individual from population
     *
     * @param population p
     */
    private static void outputReport(Population population) {
        Individual best = population.getFittest(0);
        System.out.println("Best solution stocks index: " + best.getChromosome().stream().map(String::valueOf).collect(Collectors.joining(",")));
        System.out.println("Best solution machines index: " + best.getChromosomeMachine().stream().map(String::valueOf).collect(Collectors.joining(",")));
        System.out.println("Best solution datetime cut product: " + best.getChromosomeTime().stream().map(String::valueOf).collect(Collectors.joining(",")));
        System.out.println("Best value remain: " + best.getFitness());
        System.out.println();
    }

    /**
     * Print Statistic info of fittest individual from population
     *
     * @param population p
     */
    private static void outputStatistic(Population population, List<Machine> machines, List<Stack> stacks) {
        Individual best = population.getFittest(0);
        AtomicInteger indexBeginStack = new AtomicInteger();
        stacks.forEach(stack -> {
            System.out.println("Item: " + stack.getIndexStack() + " -- Consignment: " + stack.getIndexConsign() + " -- Length: " + stack.getLength());
            List<Integer> subListIndexMachine = best.getChromosomeMachine().subList(indexBeginStack.get(), indexBeginStack.get() + stack.getQuantity());
            List<String> subListIndexTime = best.getChromosomeTime().subList(indexBeginStack.get(), indexBeginStack.get() + stack.getQuantity());
            for (int indexMachine = 0; indexMachine < machines.size(); indexMachine++) {
                int finalIndexMachine = indexMachine;
                long mount = subListIndexMachine.stream().filter(item -> item.equals(finalIndexMachine)).count();
                System.out.println("Machine " + (indexMachine + 1) + " cuts " + mount + "/" + stack.getQuantity()
                    + " of this, starting from the moment " + subListIndexTime.get(subListIndexMachine.indexOf(finalIndexMachine)));
            }
            System.out.println("====================================================================");
            indexBeginStack.addAndGet(stack.getQuantity());
        });
        System.out.println();
    }
}
