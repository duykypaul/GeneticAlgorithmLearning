package com.duykypaul.steel;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class GeneticAlgorithm {
    private int populationSize;

    /**
     * Mutation rate is the fractional probability than an individual gene will
     * mutate randomly in a given generation. The range is 0.0-1.0, but is
     * generally small (on the order of 0.1 or less).
     */
    private double mutationRate;

    /**
     * Crossover rate is the fractional probability that two individuals will
     * "mate" with each other, sharing genetic information, and creating
     * offspring with traits of each of the parents. Like mutation rate the
     * rance is 0.0-1.0 but small.
     */
    private double crossoverRate;

    /**
     * Elitism is the concept that the strongest members of the population
     * should be preserved from generation to generation. If an individual is
     * one of the elite, it will not be mutated or crossover.
     */
    private int elitismCount;

    /**
     * limits the running time of the algorithm
     */
    private int runningTimeLimit;

    public GeneticAlgorithm(int populationSize, double mutationRate, double crossoverRate, int elitismCount, int runningTimeLimit) {
        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.elitismCount = elitismCount;
        this.runningTimeLimit = runningTimeLimit;
    }

    /**
     * Initialize population
     *
     * @param stocks
     * @param orders
     * @param cutWidth
     * @param generationLimit
     * @return population The initial population generated
     */
    public Population initPopulation(List<Integer> stocks, List<Integer> orders, int cutWidth, int generationLimit) {
        return new Population(this.populationSize, stocks, orders, cutWidth, generationLimit);
    }

    /**
     * Calculate fitness for an individual.
     *
     * In this case, the fitness score is very simple: it's the number of ones
     * in the chromosome. Don't forget that this method, and this whole
     * GeneticAlgorithm class, is meant to solve the problem in the "AllOnesGA"
     * class and example. For different problems, you'll need to create a
     * different version of this method to appropriately calculate the fitness
     * of an individual.
     *
     * @param individual
     *            the individual to evaluate
     * @param stocks
     * @param orders
     * @param cutWidth
     * @return double The fitness value for individual
     */
    public double calcFitness(Individual individual, List<Integer> stocks, List<Integer> orders, int cutWidth) {
        // Track number of correct genes
        double fitness = Population.getWeightOfARN(individual.getChromosome(), stocks, orders, cutWidth);

        // Store fitness
        individual.setFitness(fitness);

        return fitness;
    }

    /**
     * Evaluate the whole population
     *
     * Essentially, loop over the individuals in the population, calculate the
     * fitness for each, and then calculate the entire population's fitness. The
     * population's fitness may or may not be important, but what is important
     * here is making sure that each individual gets evaluated.
     *
     * @param population
     *            the population to evaluate
     */
    public void evalPopulation(Population population) {
        double populationFitness = 0;
        this.setPopulationSize(population.getIndividuals().size());
        // Loop over population evaluating individuals and summing population
        // fitness
        for (Individual individual : population.getIndividuals()) {
            populationFitness += calcFitness(individual, Population.stocks, Population.orders, Population.CUT_WIDTH);
        }

        double populationFitnessAvg = populationFitness / populationSize;

        population.setPopulationFitness(populationFitnessAvg);
    }

    /**
     * Check if population has met termination condition
     *
     * For this simple problem, we know what a perfect solution looks like, so
     * we can simply stop evolving once we've reached a fitness of one.
     *
     * @param population
     * @param start
     * @return boolean True if termination condition met, otherwise, false
     */
    public boolean isTerminationConditionMet(Population population, Instant start) {
        /*for (Individual individual : population.getIndividuals()) {
            if (individual.getFitness() == 1) {
                return true;
            }
        }*/

        return Duration.between(start, Instant.now()).getSeconds() > this.runningTimeLimit;
    }

    /**
     * Select parent for crossover
     *
     * @param population
     *            The population to select parent from
     * @return The individual selected as a parent
     */
    public Individual selectParent(Population population) {
        // Get individuals
        List<Individual> individuals = population.getIndividuals();

        // Spin roulette wheel
        double populationFitness = population.getPopulationFitness();
        double rouletteWheelPosition = Math.random() * populationFitness;

        // Find parent
        double spinWheel = 0;
        for (Individual individual : individuals) {
            spinWheel += individual.getFitness();
            if (spinWheel >= rouletteWheelPosition) {
                return individual;
            }
        }
        return individuals.get(population.size() - 1);
    }

    /**
     * Apply crossover to population
     *
     * Crossover, more colloquially considered "mating", takes the population
     * and blends individuals to create new offspring. It is hoped that when two
     * individuals crossover that their offspring will have the strongest
     * qualities of each of the parents. Of course, it's possible that an
     * offspring will end up with the weakest qualities of each parent.
     *
     * This method considers both the GeneticAlgorithm instance's crossoverRate
     * and the elitismCount.
     *
     * The type of crossover we perform depends on the problem domain. We don't
     * want to create invalid solutions with crossover, so this method will need
     * to be changed for different types of problems.
     *
     * This particular crossover method selects random genes from each parent.
     *
     * @param population The population to apply crossover to
     * @return The new population
     */
    public Population crossoverPopulation(Population population) {
        // Create new population
        Population newPopulation = new Population(population);

        // Loop over current population by fitness
        for (int populationIndex = 0; populationIndex < population.size(); populationIndex++) {
            Individual parent1 = population.getFittest(populationIndex);

            // Apply crossover to this individual?
            if (this.crossoverRate > Math.random() && populationIndex >= this.elitismCount) {
                // Initialize offspring
                Individual offspring = new Individual(parent1.getChromosome());

                // Find second parent
                Individual parent2 = selectParent(population);

                // Loop over genome
                for (int geneIndex = 0; geneIndex < parent1.getChromosomeLength(); geneIndex++) {
                    // Use half of parent1's genes and half of parent2's genes
                    if (0.5 > Math.random()) {
                        offspring.setGene(geneIndex, parent1.getGene(geneIndex));
                    } else {
                        offspring.setGene(geneIndex, parent2.getGene(geneIndex));
                    }
                }

                // Add offspring to new population
                newPopulation.setIndividual(populationIndex, offspring);
            } else {
                // Add individual to new population without applying crossover
                newPopulation.setIndividual(populationIndex, parent1);
            }
        }

        return newPopulation;
    }

    /**
     * Apply mutation to population.
     *
     * Mutation affects individuals rather than the population. We look at each
     * individual in the population, and if they're lucky enough (or unlucky, as
     * it were), apply some randomness to their chromosome. Like crossover, the
     * type of mutation applied depends on the specific problem we're solving.
     * In this case, we simply randomly flip 0s to 1s and vice versa.
     *
     * This method will consider the GeneticAlgorithm instance's mutationRate
     * and elitismCount
     *
     * @param population
     *            The population to apply mutation to
     * @return The mutated population
     */
    public Population mutatePopulation(Population population) {
        // Initialize new population
        Population newPopulation = new Population(population);

        int maxRandomInt = Population.ARNsN - 1;
        // Loop over current population by fitness
        int mutationARNsN = (int) (Population.ARNsN * this.mutationRate);
        for (int i = 0; i < mutationARNsN; ++i) {
            Integer worstPosition = findWorstPositionInPopulation(newPopulation.getIndividuals(), this.populationSize);
            int randInt = getRandomNumber(this.elitismCount + 1, maxRandomInt);
            int worstFitness = Population.getWeightOfARN(
                newPopulation.getIndividuals().get(worstPosition).getChromosome(),
                Population.stocks, Population.orders, Population.CUT_WIDTH);
            mutate(newPopulation, newPopulation.getIndividuals().get(randInt).getChromosome(), worstPosition, worstFitness);
        }

        // Return mutated population
        return newPopulation;
    }

    Integer findWorstPositionInPopulation(List<Individual> individuals, int populationSize) {
        int worstValue = Integer.MIN_VALUE;
        int worstARNPosition = 0;

        for (int i = 0; i < populationSize; ++i) {
            int currValue = Population.getWeightOfARN(individuals.get(i).getChromosome(), Population.stocks, Population.orders, Population.CUT_WIDTH);
            if (currValue > worstValue) {
                worstARNPosition = i;
                worstValue = currValue;
            }
        }

        return worstARNPosition;
    }

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    void mutate(Population newPopulation, List<Integer> ARN, int worstPosition, int worstValue) {
        List<Integer> stockTemp = Population.computeStockRemain(ARN, Population.stocks, Population.orders, Population.CUT_WIDTH);
        //todo thinking bestGapOfAll for what?
        int bestGapOfAll = Integer.MAX_VALUE;
        //todo thinking finalMoveTo for what?
        int finalMoveTo = -1;
        //todo thinking finalFromPosition for what?
        int finalFromPosition = -1;

        for (int i = 0; i < Population.orders.size(); ++i) {
            int bestGap = Integer.MAX_VALUE;
            int moveTo = -1;
            int fromPosition = -1;
            for (int j = 0; j < Population.stocks.size(); ++j) {
                if (j == ARN.get(i)) {
                    continue;
                }
                if (stockTemp.get(j) >= Population.orders.get(i)) {
                    if (stockTemp.get(j).equals(Population.orders.get(i))) {
                        if (bestGap > -stockTemp.get(ARN.get(i))) {
                            bestGap = -stockTemp.get(ARN.get(i));
                            moveTo = j;
                            fromPosition = i;
                        }
                    }
                    if (stockTemp.get(j) >= Population.orders.get(i) + Population.CUT_WIDTH) {
                        int number = (stockTemp.get(j) - (Population.orders.get(i) + Population.CUT_WIDTH)) - stockTemp.get(ARN.get(i));
                        if (bestGap > number) {
                            bestGap = number;
                            moveTo = j;
                            fromPosition = i;
                        }
                    }
                }
            }

            if (bestGapOfAll > bestGap) {
                bestGapOfAll = bestGap;
                finalMoveTo = moveTo;
                finalFromPosition = fromPosition;
            }
        }

        if (finalFromPosition < 0 || finalMoveTo < 0) {
            return;
        }

        ARN.set(finalFromPosition, finalMoveTo);

        // mutant individual
        Individual mutant = new Individual(ARN);
        mutant.setFitness(Population.getWeightOfARN(ARN, Population.stocks, Population.orders, Population.CUT_WIDTH));

        if (mutant.getFitness() < worstValue) {
            newPopulation.getIndividuals().set(worstPosition, mutant);
        }
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public double getMutationRate() {
        return mutationRate;
    }

    public void setMutationRate(double mutationRate) {
        this.mutationRate = mutationRate;
    }

    public double getCrossoverRate() {
        return crossoverRate;
    }

    public void setCrossoverRate(double crossoverRate) {
        this.crossoverRate = crossoverRate;
    }

    public int getElitismCount() {
        return elitismCount;
    }

    public void setElitismCount(int elitismCount) {
        this.elitismCount = elitismCount;
    }

    public Population initPopulation(List<Integer> stocks, List<LocalDate> stocksDate, List<Integer> orders,
                                     List<LocalDate> ordersDate, List<Machine> machines, int cutWidth, int generationLimit) {
        return new Population(this.populationSize, stocks, stocksDate, orders, ordersDate, machines, cutWidth, generationLimit);
    }
}
