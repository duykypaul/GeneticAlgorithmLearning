package com.duykypaul.arn;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


/**
 * A population is an abstraction of a collection of individuals. The population
 * class is generally used to perform group-level operations on its individuals,
 * such as finding the strongest individuals, collecting stats on the population
 * as a whole, and selecting individuals to mutate or crossover.
 *
 * @author duykypaul
 */
public class Population {
    private int realPopulationSize = 0;
    private List<Integer> stocks;
    private List<Integer> orders;
    private int generationLimit;
    private int cutWidth;
    private Instant startGen;
    private Boolean isFinishedGen = false;

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
        this.startGen = populationClone.startGen;
        this.realPopulationSize = populationClone.realPopulationSize;
        this.generationLimit = populationClone.generationLimit;
        this.cutWidth = populationClone.cutWidth;
        this.stocks = populationClone.stocks;
        this.orders = populationClone.orders;
    }

    /**
     * Initializes population of individuals
     *
     * @param populationSize  The number of individuals in the population
     * @param stocksInit      stock input
     * @param ordersInit      order input
     * @param cutWidth        width sword
     * @param generationLimit population creation time
     */
    public Population(int populationSize, List<Integer> stocksInit, List<Integer> ordersInit, int cutWidth, int generationLimit) {
        // Initialize the population as an array of individuals
        this.population = new ArrayList<>();
        stocks = new ArrayList<>(stocksInit);
        orders = new ArrayList<>(ordersInit);
        this.generationLimit = generationLimit;
        this.cutWidth = cutWidth;

        /*
         * stores a list of index of material used to cut the product
         */
        List<Integer> chromosome = new ArrayList<>();


        List<Integer> stockState = new ArrayList<>(stocksInit);

        startGen = Instant.now();
        generatePopulation(chromosome, stockState);

        if (this.realPopulationSize > populationSize) {
            this.population = this.population.subList(0, populationSize);
            this.realPopulationSize = populationSize;
        }
    }

    public int getFitnessOfChromosome(List<Integer> currentChromosome, List<Integer> stocks, List<Integer> orders) {
        List<Integer> uniqueStocks = currentChromosome.stream().distinct().sorted().collect(Collectors.toList());
        List<Integer> stockTemp = getStockRemain(currentChromosome, stocks, orders);

        boolean isContainNegative = stockTemp.stream().parallel().anyMatch(item -> item < 0);
        if (isContainNegative) {
            System.out.println("Negative contain");
            return Integer.MAX_VALUE;
        }

        int value = 0;
        for (Integer uniqueStock : uniqueStocks) {
            value += stockTemp.get(uniqueStock);
        }

        return value;
    }

    public List<Integer> getStockRemain(List<Integer> currentARNStocks, List<Integer> stocks, List<Integer> orders) {
        List<Integer> stockTemp = new ArrayList<>(stocks);
        for (int i = 0; i < orders.size(); ++i) {
            if (stockTemp.get(currentARNStocks.get(i)).equals(orders.get(i))) {
                stockTemp.set(currentARNStocks.get(i), stockTemp.get(currentARNStocks.get(i)) - orders.get(i));
            } else {
                stockTemp.set(currentARNStocks.get(i), stockTemp.get(currentARNStocks.get(i)) - (orders.get(i) + this.cutWidth));
            }
        }
        return stockTemp;
    }

    void generatePopulation(List<Integer> currentChromosome, List<Integer> stockState) {
        // Population limit reached!
        if (Boolean.TRUE.equals(isFinishedGen)) {
            return;
        }
        long duration = Duration.between(startGen, Instant.now()).getSeconds();
        if (duration > this.generationLimit) {
            isFinishedGen = true;
            return;
        }

        // Get a complete ARN
        if (currentChromosome.size() == orders.size()) {
            Individual individual = new Individual(new ArrayList<>(currentChromosome));
            int weight = getFitnessOfChromosome(currentChromosome, stocks, orders);
            individual.setFitness(weight);
            this.population.add(individual);
            this.realPopulationSize++;
            return;
        }
        // Next Node
        for (int i = 0; i < stockState.size(); i++) {
            duration = Duration.between(startGen, Instant.now()).getSeconds();
            if (duration > this.generationLimit) {
                isFinishedGen = true;
                return;
            }

            int idx = currentChromosome.size();
            if (orders.get(idx).equals(stockState.get(i))) {
                List<Integer> stocksTemp = new ArrayList<>(stockState);
                // update stocks
                stocksTemp.set(i, stocksTemp.get(i) - orders.get(idx));

                currentChromosome.add(i);

                generatePopulation(currentChromosome, stocksTemp);
                currentChromosome.remove(currentChromosome.size() - 1);

                duration = Duration.between(startGen, Instant.now()).getSeconds();
                if (duration > this.generationLimit) {
                    isFinishedGen = true;
                    return;
                }
            } else {

                if (orders.get(idx) + this.cutWidth <= stockState.get(i)) {
                    List<Integer> stocksTemp = new ArrayList<>(stockState);
                    // update stocks state
                    stocksTemp.set(i, stocksTemp.get(i) - orders.get(idx) - this.cutWidth);

                    // add to ARN
                    currentChromosome.add(i);

                    generatePopulation(currentChromosome, stocksTemp);
                    // redo add to ARN
                    currentChromosome.remove(currentChromosome.size() - 1);

                    if (Boolean.TRUE.equals(isFinishedGen)) {
                        return;
                    }
                    duration = Duration.between(startGen, Instant.now()).getSeconds();
                    if (duration > this.generationLimit) {
                        isFinishedGen = true;
                        return;
                    }
                }
            }
        }
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
     * @param individual individual
     * @param offset     offset
     * @return individual
     */
    public Individual setIndividual(int offset, Individual individual) {
        return population.set(offset, individual);
    }

    /**
     * Get individual at offset
     *
     * @param offset offset
     * @return individual
     */
    public Individual getIndividual(int offset) {
        return population.get(offset);
    }

    public int getRealPopulationSize() {
        return realPopulationSize;
    }

    public void setRealPopulationSize(int realPopulationSize) {
        this.realPopulationSize = realPopulationSize;
    }

    public int getGenerationLimit() {
        return generationLimit;
    }

    public void setGenerationLimit(int generationLimit) {
        this.generationLimit = generationLimit;
    }

    public Instant getStartGen() {
        return startGen;
    }

    public void setStartGen(Instant startGen) {
        this.startGen = startGen;
    }

    public Boolean getFinishedGen() {
        return isFinishedGen;
    }

    public void setFinishedGen(Boolean finishedGen) {
        isFinishedGen = finishedGen;
    }

    public List<Individual> getPopulation() {
        return population;
    }

    public void setPopulation(List<Individual> population) {
        this.population = population;
    }

    public int getCutWidth() {
        return cutWidth;
    }

    public void setCutWidth(int cutWidth) {
        this.cutWidth = cutWidth;
    }

    public List<Integer> getStocks() {
        return stocks;
    }

    public void setStocks(List<Integer> stocks) {
        this.stocks = stocks;
    }

    public List<Integer> getOrders() {
        return orders;
    }

    public void setOrders(List<Integer> orders) {
        this.orders = orders;
    }
}
