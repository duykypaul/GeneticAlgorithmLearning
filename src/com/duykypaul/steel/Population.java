package com.duykypaul.steel;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


/**
 * A population is an abstraction of a collection of individuals. The population
 * class is generally used to perform group-level operations on its individuals,
 * such as finding the strongest individuals, collecting stats on the population
 * as a whole, and selecting individuals to mutate or crossover.
 *
 * @author bkanber
 */
public class Population {
    static int ARNsN = 0;
    static List<Integer> stocks;
    static List<Integer> orders;
    static int CUT_WIDTH;
    static long GENERATION_LIMIT;
    Instant start_gen;
    Boolean isFinishedGen = false;

    private List<Individual> population;
    private double populationFitness = -1;

    /**
     * Initializes population of individuals
     *
     * @param populationClone The population of individuals
     */
    public Population(Population populationClone) {
        // Initial population
        this.population = populationClone.population;
        this.populationFitness = populationClone.populationFitness;
        this.isFinishedGen = populationClone.isFinishedGen;
        this.start_gen = populationClone.start_gen;
        /*this.ARNsN = Population.ARNsN;
        this.stocks = Population.stocks;
        this.orders = Population.orders;
        this.CUT_WIDTH = Population.CUT_WIDTH;*/
    }

    /**
     * Initializes population of individuals
     *
     * @param populationSize The number of individuals in the population
     * @param stocksInit
     * @param ordersInit
     */
    public Population(int populationSize, List<Integer> stocksInit, List<Integer> ordersInit, int cutWidth, int generationLimit) {
        // Initialize the population as an array of individuals
        this.population = new ArrayList<>();
        stocks = new ArrayList<>(stocksInit);
        orders = new ArrayList<>(ordersInit);
        CUT_WIDTH = cutWidth;
        GENERATION_LIMIT = generationLimit;
        List<Integer> ARN = new ArrayList<>();
        List<Integer> stockState = new ArrayList<>(stocksInit);

        start_gen = Instant.now();
        generateARN(ARN, stockState);

        if (ARNsN > populationSize) {
            this.population = this.population.subList(0, populationSize);
            ARNsN = populationSize;
        }
    }

    public static int getWeightOfARN(List<Integer> currARN, List<Integer> stocks, List<Integer> orders, int CUT_WIDTH) {
        List<Integer> uniqueStocks = currARN.stream().distinct().sorted().collect(Collectors.toList());
        List<Integer> stockTemp = computeStockRemain(currARN, stocks, orders, CUT_WIDTH);

        int value = 0;
        for (Integer uniqueStock : uniqueStocks) {
            value += stockTemp.get(uniqueStock);
        }

        return value;
    }

    public static List<Integer> computeStockRemain(List<Integer> ARN, List<Integer> stocks, List<Integer> orders, int CUT_WIDTH) {
        List<Integer> stockTemp = new ArrayList<>(stocks);
        for (int i = 0; i < orders.size(); ++i) {
            if (stockTemp.get(ARN.get(i)).equals(orders.get(i))) {
                stockTemp.set(ARN.get(i), stockTemp.get(ARN.get(i)) - orders.get(i));
            } else {
                stockTemp.set(ARN.get(i), stockTemp.get(ARN.get(i)) - (orders.get(i) + CUT_WIDTH));
            }
        }
        return stockTemp;
    }

    /**
     * Get individuals from the population
     *
     * @return individuals Individuals in population
     */
    public List<Individual> getIndividuals() {
        return this.population;
    }

    /**
     * Find an individual in the population by its fitness
     * <p>
     * This method lets you select an individual in order of its fitness. This
     * can be used to find the single strongest individual (eg, if you're
     * testing for a solution), but it can also be used to find weak individuals
     * (if you're looking to cull the population) or some of the strongest
     * individuals (if you're using "elitism").
     *
     * @param offset The offset of the individual you want, sorted by fitness. 0 is
     *               the strongest, population.length - 1 is the weakest.
     * @return individual Individual at offset
     */
    public Individual getFittest(int offset) {
        // Order population by fitness
        this.population.sort(Comparator.comparing(Individual::getFitness));

        // Return the fittest individual
        return this.population.get(offset);
    }

    /**
     * Get population's group fitness
     *
     * @return populationFitness The population's total fitness
     */
    public double getPopulationFitness() {
        return this.populationFitness;
    }

    /**
     * Set population's group fitness
     *
     * @param fitness The population's total fitness
     */
    public void setPopulationFitness(double fitness) {
        this.populationFitness = fitness;
    }

    /**
     * Get population's size
     *
     * @return size The population's size
     */
    public int size() {
        return this.population.size();
    }

    /**
     * Set individual at offset
     *
     * @param individual
     * @param offset
     * @return individual
     */
    public Individual setIndividual(int offset, Individual individual) {
        return population.set(offset, individual);
    }

    /**
     * Get individual at offset
     *
     * @param offset
     * @return individual
     */
    public Individual getIndividual(int offset) {
        return population.get(offset);
    }

    /**
     * Shuffles the population in-place
     *
     * @return void
     */
    public void shuffle() {
        Random rnd = new Random();
        for (int i = population.size() - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            Individual a = population.get(index);
            population.set(index, population.get(i));
            population.set(i, a);
        }
    }

    void generateARN(List<Integer> currARN, List<Integer> stockState) {
        // Population limit reached!
        if (isFinishedGen) {
            return;
        }
        long duration = Duration.between(start_gen, Instant.now()).getSeconds();
        if (duration > GENERATION_LIMIT) {
            isFinishedGen = true;
            return;
        }

        // Get a complete ARN
        if (currARN.size() == orders.size()) {
            Individual individual = new Individual(new ArrayList<>(currARN));
            int weight = getWeightOfARN(currARN, stocks, orders, CUT_WIDTH);
            individual.setFitness(weight);
            this.population.add(individual);
            ARNsN++;
            return;
        }
        // Next Node
        for (int i = 0; i < stockState.size(); i++) {
            /*if (isFinishedGen) {
                return;
            }
            duration = Duration.between(start_gen, Instant.now()).getSeconds();
            if (duration > GENERATION_LIMIT) {
                isFinishedGen = true;
                return;
            }*/

            int idx = currARN.size();

            if (orders.get(idx).equals(stockState.get(i))) {
                List<Integer> stockTemp = new ArrayList<>(stockState);
                stockTemp.set(i, stockTemp.get(i) - orders.get(idx));
                currARN.add(i);
                generateARN(currARN, stockTemp);
                currARN.remove(currARN.size() - 1);

                /*if (isFinishedGen) {
                    return;
                }
                duration = Duration.between(start_gen, Instant.now()).getSeconds();
                if (duration > GENERATION_LIMIT) {
                    isFinishedGen = true;
                    return;
                }*/
            } else {
                if (orders.get(idx) + CUT_WIDTH <= stockState.get(i)) {
                    List<Integer> stockTemp = new ArrayList<>(stockState);
                    stockTemp.set(i, stockTemp.get(i) - orders.get(idx) - CUT_WIDTH);
                    currARN.add(i);
                    generateARN(currARN, stockTemp);
                    currARN.remove(currARN.size() - 1);
                    /*if (isFinishedGen) {
                        return;
                    }
                    duration = Duration.between(start_gen, Instant.now()).getSeconds();
                    if (duration > GENERATION_LIMIT) {
                        isFinishedGen = true;
                        return;
                    }*/
                }
            }

            /*duration = Duration.between(start_gen, Instant.now()).getSeconds();
            if (duration > GENERATION_LIMIT) {
                isFinishedGen = true;
                return;
            }*/
        }
    }
}
