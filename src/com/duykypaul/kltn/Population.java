package com.duykypaul.kltn;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
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
    static List<LocalDate> stocksDate;
    static List<Integer> orders;
    static List<LocalDate> ordersDate;
    static List<Machine> machines;
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
     * @param stocksInit stock input
     * @param stocksDateInit stock input date
     * @param ordersInit order input
     * @param ordersDateInit order input date
     * @param machinesInit machines date
     * @param generationLimit population creation time
     */
    public Population(int populationSize, List<Integer> stocksInit, List<LocalDate> stocksDateInit, List<Integer> ordersInit,
                      List<LocalDate> ordersDateInit, List<Machine> machinesInit, int generationLimit) {
        // Initialize the population as an array of individuals
        this.population = new ArrayList<>();
        stocks = new ArrayList<>(stocksInit);
        stocksDate = new ArrayList<>(stocksDateInit);
        orders = new ArrayList<>(ordersInit);
        ordersDate = new ArrayList<>(ordersDateInit);
        machines = new ArrayList<>(machinesInit);
        GENERATION_LIMIT = generationLimit;

        /**
         * stores a list of index of material used to cut the product
         */
        List<Integer> ARNStocks = new ArrayList<>();
        /**
         * stores a list of index of machine used to cut the product
         */
        List<Integer> ARNMachines = new ArrayList<>();
        List<Integer> stockState = new ArrayList<>(stocksInit);
        List<Machine> machinesState = new ArrayList<>(machinesInit);

        start_gen = Instant.now();
        generateARNV3(ARNStocks, ARNMachines, stockState, machinesState);

        if (ARNsN > populationSize) {
            this.population = this.population.subList(0, populationSize);
            ARNsN = populationSize;
        }
    }
    
    public static int getWeightOfARNV3(List<Integer> currentARNStocks, List<Integer> currentARNMachines, List<Integer> stocks, List<Integer> orders) {
        List<Integer> uniqueStocks = currentARNStocks.stream().distinct().sorted().collect(Collectors.toList());
        List<Integer> stockTemp = computeStockRemainV3(currentARNStocks, currentARNMachines, stocks, orders);

        int value = 0;
        for (Integer uniqueStock : uniqueStocks) {
            value += stockTemp.get(uniqueStock);
        }

        return value;
    }

    public static List<Integer> computeStockRemainV3(List<Integer> currentARNStocks, List<Integer> currentARNMachines, List<Integer> stocks, List<Integer> orders) {
        List<Integer> stockTemp = new ArrayList<>(stocks);
        for (int i = 0; i < orders.size(); ++i) {
            if (stockTemp.get(currentARNStocks.get(i)).equals(orders.get(i))) {
                stockTemp.set(currentARNStocks.get(i), stockTemp.get(currentARNStocks.get(i)) - orders.get(i));
            } else {
                stockTemp.set(currentARNStocks.get(i), stockTemp.get(currentARNStocks.get(i)) - (orders.get(i) + machines.get(currentARNMachines.get(i)).getBladeThickness()));
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

    void generateARNV3(List<Integer> currentARNStocks, List<Integer> currentARNMachines, List<Integer> stockState, List<Machine> machinesState) {
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
        if (currentARNStocks.size() == orders.size()) {
            Individual individual = new Individual(new ArrayList<>(currentARNStocks), new ArrayList<>(currentARNMachines));
            int weight = getWeightOfARNV3(currentARNStocks, currentARNMachines, stocks, orders);
            individual.setFitness(weight);
            this.population.add(individual);
            ARNsN++;
            return;
        }
        // Next Node
        for (int i = 0; i < stockState.size(); i++) {
            int idx = currentARNStocks.size();
            int indexMostFree = getMostFreeIndexForCurrentState(machinesState, i, idx);
            if(indexMostFree == -1) {
                return;
            }
            if (orders.get(idx).equals(stockState.get(i))) {
                List<Integer> stocksTemp = new ArrayList<>(stockState);
                List<Machine> machinesTemp = new ArrayList<>(machinesState);
                // update stocks
                stocksTemp.set(i, stocksTemp.get(i) - orders.get(idx));

                currentARNStocks.add(i);
                currentARNMachines.add(indexMostFree);

                generateARNV3(currentARNStocks, currentARNMachines, stocksTemp, machinesTemp);

                currentARNStocks.remove(currentARNStocks.size() - 1);
                currentARNMachines.remove(currentARNMachines.size() - 1);
            } else {
                int cutWidth = machinesState.get(indexMostFree).getBladeThickness();
                if (orders.get(idx) + cutWidth <= stockState.get(i)) {
                    List<Integer> stocksTemp = new ArrayList<>(stockState);
                    List<Machine> machinesTemp = new ArrayList<>(machinesState);
                    // update stocks state
                    stocksTemp.set(i, stocksTemp.get(i) - orders.get(idx) - cutWidth);

                    // update machines state
                    Machine currentMachine = machinesTemp.get(indexMostFree);
                    int minutesRemain = currentMachine.getMinutesRemaining() - currentMachine.getFrequency();
                    if(minutesRemain > Machine.MIN_MINUTES_REMAINING) {
                        currentMachine.setMinutesRemaining(minutesRemain);
                    } else {
                        currentMachine.setMinutesRemaining(Machine.MAX_MINUTES_REMAINING);
                        currentMachine.plusDay();
                    }

                    // add to ARN
                    currentARNStocks.add(i);
                    currentARNMachines.add(indexMostFree);

                    generateARNV3(currentARNStocks, currentARNMachines, stocksTemp, machinesTemp);

                    // redo add to ARN
                    currentARNStocks.remove(currentARNStocks.size() - 1);
                    currentARNMachines.remove(currentARNMachines.size() - 1);
                }
            }
        }
    }

    private int getMostFreeIndexForCurrentState(List<Machine> machinesState, int stockIndex, int orderIndex) {
        Optional<Machine> machines = machinesState.stream()
            .sorted(Comparator
                .comparing(Machine::getFreeDate)
                .thenComparingInt(Machine::getMinutesRemaining).reversed()
            )
            .filter(o -> o.getFreeDate().compareTo(ordersDate.get(orderIndex)) < 0 && stocksDate.get(stockIndex).compareTo(ordersDate.get(orderIndex)) < 0)
            .findFirst();
        return machinesState.indexOf(machines.get());
    }
}
