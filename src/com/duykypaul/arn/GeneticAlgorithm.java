package com.duykypaul.arn;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GeneticAlgorithm {
    private int populationSize;
    /**
     * Mutation rate is the fractional probability than an individual gene will
     * mutate randomly in a given generation. The range is 0.0-1.0, but is
     * generally small (on the order of 0.1 or less).
     */
    private final double mutationRate;
    /**
     * Crossover rate is the fractional probability that two individuals will
     * "mate" with each other, sharing genetic information, and creating
     * offspring with traits of each of the parents. Like mutation rate the
     * rance is 0.0-1.0 but small.
     */
    private final double crossoverRate;
    /**
     * Elitism is the concept that the strongest members of the population
     * should be preserved from generation to generation. If an individual is
     * one of the elite, it will not be mutated or crossover.
     */
    private final int elitismCount;
    /**
     * Worst rate is the proportion of bad individuals that should be in a population.
     * The range is 0.0-1.0, but is generally small (on the order of 0.1 or less).
     */
    private final double worstRate;
    /**
     * limits the running time of the algorithm
     */
    private final int runningTimeLimit;

    private final int generationSameLimit;

    private static final Random randomNumber = new Random();

    public GeneticAlgorithm(int populationSize, double mutationRate, double crossoverRate, int elitismCount, double worstRate, int runningTimeLimit, int generationSameLimit) {
        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.elitismCount = elitismCount;
        this.runningTimeLimit = runningTimeLimit;
        this.worstRate = worstRate;
        this.generationSameLimit = generationSameLimit;
    }

    public static int getRandomNumber(int min, int max) {
        return randomNumber.nextInt(max - min) + min;
    }

    /**
     * Initialize population
     *
     * @param stocks          stocks
     * @param orders          orders
     * @param generationLimit generationLimit
     * @return population The initial population generated
     */
    public Population initPopulation(List<Integer> stocks, List<Integer> orders, int cutWidth, int generationLimit) {
        return new Population(this.populationSize, stocks, orders, cutWidth, generationLimit);
    }

    /**
     * Calculate fitness for an individual.
     * <p>
     * In this case, the fitness score is very simple: it's the sum of the rest of the
     * processed bars (shown in the primary chromosome).
     *
     * @param population
     * @param individual the individual to evaluate
     * @param stocks     stocks
     * @param orders     orders
     * @return double The fitness value for individual
     */
    public double calcFitness(Population population, Individual individual, List<Integer> stocks, List<Integer> orders) {
        // Track number of correct genes
        double fitness = population.getFitnessOfChromosome(individual.getChromosome(), stocks, orders);

        // Store fitness
        individual.setFitness(fitness);

        return fitness;
    }

    /**
     * Evaluate the whole population
     * <p>
     * Essentially, loop over the individuals in the population, calculate the
     * fitness for each, and then calculate the entire population's fitness. The
     * population's fitness may or may not be important, but what is important
     * here is making sure that each individual gets evaluated.
     *
     * @param population the population to evaluate
     */
    public void evalPopulation(Population population) {
        double populationFitness = 0;
        this.setPopulationSize(population.getIndividuals().size());
        // Loop over population evaluating individuals and summing population
        // fitness
        for (Individual individual : population.getIndividuals()) {
//            populationFitness += calcFitness(population, individual, population.getStocks(), population.getOrders());
            populationFitness += individual.getFitness();
        }

        double populationFitnessAvg = populationFitness / populationSize;

        population.setPopulationFitness(populationFitnessAvg);
    }

    /**
     * Check if population has met termination condition
     * <p>
     * For this problem, we know what a perfect solution looks like, so
     * we can simply stop evolving once we've reached a fitness of one.
     *
     * @param population population
     * @param start      start
     * @param resultSet  resultSet
     * @return boolean True if termination condition met, otherwise, false
     */
    public boolean isTerminationConditionMet(Population population, Instant start, SortedMap<Double, Integer> resultSet) {
        double fitness = population.getFittest(0).getFitness();
        resultSet.merge(fitness, 1, Integer::sum);
        if (resultSet.get(resultSet.firstKey()) >= this.generationSameLimit) {
            return true;
        }

        return Duration.between(start, Instant.now()).getSeconds() > this.runningTimeLimit;
    }

    /**
     * Select parent for crossover
     *
     * @param population The population to select parent from
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
     * <p>
     * Crossover, more colloquially considered "mating", takes the population
     * and blends individuals to create new offspring. It is hoped that when two
     * individuals crossover that their offspring will have the strongest
     * qualities of each of the parents. Of course, it's possible that an
     * offspring will end up with the weakest qualities of each parent.
     * <p>
     * This method considers both the GeneticAlgorithm instance's crossoverRate
     * and the elitismCount.
     * <p>
     * The type of crossover we perform depends on the problem domain. We don't
     * want to create invalid solutions with crossover, so this method will need
     * to be changed for different types of problems.
     * <p>
     * This particular crossover method selects random genes from each parent.
     *
     * @param population The population to apply crossover to
     * @return The new population
     */
    public Population crossoverPopulation(Population population) {
        System.out.println("===========IN THE PROCESS OF CROSSOVER===========");
        // Create new population
        Population newPopulation = new Population(population);

        List<Integer> orders = newPopulation.getOrders();
        List<Integer> stocks = newPopulation.getStocks();

        // Loop over current population by fitness
        for (int populationIndex = this.elitismCount; populationIndex < population.size(); populationIndex++) {
            Individual parent1 = population.getFittest(populationIndex);

            // Apply crossover to this individual?
            if (this.crossoverRate > Math.random()) {
                // Initialize offspring
                Individual offspring = new Individual(parent1);
                List<Integer> stockRemain = newPopulation.getStockRemain(parent1.getChromosome(), stocks, orders);

                // Find second parent
                Individual parent2 = selectParent(population);

                // Loop over genome
                for (int geneIndex = 0; geneIndex < parent1.getChromosomeLength(); geneIndex++) {
                    // Use half of parent1's genes and half of parent2's genes
                    int gene1 = offspring.getGene(geneIndex);
                    int gene2 = parent2.getGene(geneIndex);
                    // The father's genes are different from the mother's, and the mother's genes are qualified for replacement
                    if (gene1 != gene2 && stockRemain.get(gene2) >= orders.get(geneIndex)) {
                        if (stockRemain.get(gene2).equals(orders.get(geneIndex))) {
                            stockRemain.set(gene2, 0);
                            offspring.setGene(geneIndex, gene2);
                        }
                        int cutWidth = newPopulation.getCutWidth();
                        if (stockRemain.get(gene2) >= orders.get(geneIndex) + cutWidth) {
                            stockRemain.set(gene2, stockRemain.get(gene2) - orders.get(geneIndex) - cutWidth);
                            offspring.setGene(geneIndex, gene2);
                        }
                    }
                }

                double currentFitness = newPopulation.getFitnessOfChromosome(offspring.getChromosome(), stocks, orders);

                if (currentFitness < offspring.getFitness()) {
                    offspring.setFitness(currentFitness);
                    // Add offspring to new population
                    newPopulation.setIndividual(populationIndex, offspring);
                }
            } else {
                // Add individual to new population without applying crossover
                newPopulation.setIndividual(populationIndex, parent1);
            }
        }
        return newPopulation;
    }

    /**
     * Apply mutation to population.
     * <p>
     * Mutation affects individuals rather than the population. We look at each
     * individual in the population, and if they're lucky enough (or unlucky, as
     * it were), apply some randomness to their chromosome. Like crossover, the
     * type of mutation applied depends on the specific problem we're solving.
     * In this case, we simply randomly flip 0s to 1s and vice versa.
     * <p>
     * This method will consider the GeneticAlgorithm instance's mutationRate
     * and elitismCount
     *
     * @param population The population to apply mutation to
     * @return The mutated population
     */
    public Population mutatePopulationOld(Population population) {
        System.out.println("===========IN THE PROCESS OF MUTATION===========");
        // Initialize new population
        Population newPopulation = new Population(population);

        // Loop over current population by fitness
        for (int populationIndex = 0; populationIndex < population.size(); populationIndex++) {
            Individual individual = population.getFittest(populationIndex);

            // Skip mutation if this is an elite individual
            if (populationIndex >= this.elitismCount) {
                // System.out.println("Mutating population member "+populationIndex);
                // Loop over individual's genes
                mutateIndividual(newPopulation, individual);

            }

            // Add individual to population
            newPopulation.setIndividual(populationIndex, individual);
        }

        // Return mutated population
        return newPopulation;
    }

    private void mutateIndividual(Population newPopulation, Individual individualInit) {
        List<Integer> chromosome = individualInit.getChromosome();

        List<Integer> orders = newPopulation.getOrders();
        List<Integer> stocks = newPopulation.getStocks();

        int minOrder = orders.stream().min(Integer::compare).orElse(0);

        final List<Integer>[] stocksRemain = new List[]{newPopulation.getStockRemain(chromosome, stocks, orders)};

        Set<Integer> uniqueStocks = new HashSet<>(chromosome);

        for (Integer stockIndex : uniqueStocks) {
            if (this.mutationRate > Math.random()) {
                final Integer[] remainValue = {stocksRemain[0].get(stockIndex)};
                if (remainValue[0] >= minOrder) {
                    Set<Integer> uniqueStocksAnother = uniqueStocks.stream().filter(item -> !item.equals(stockIndex)).collect(Collectors.toSet());
                    for (Integer stockAnotherIndex : uniqueStocksAnother) {
                        int sumCurrentOrder = getTotalOrderByStockIndex(stockAnotherIndex, chromosome, orders, newPopulation.getCutWidth());

                        if (remainValue[0] >= sumCurrentOrder) {
                            chromosome.replaceAll(gene -> gene.equals(stockAnotherIndex) ? stockIndex : gene);
                            remainValue[0] -= sumCurrentOrder;
//                            uniqueStocks.remove(stockAnotherIndex);
                        }
                    }
                    stocksRemain[0] = newPopulation.getStockRemain(chromosome, stocks, orders);
                }
            }
        }
        double fitness = newPopulation.getFitnessOfChromosome(chromosome, stocks, orders);
        individualInit.setFitness(fitness);
    }

    private int getTotalOrderByStockIndex(Integer stockAnotherIndex, List<Integer> chromosome, List<Integer> orders, int cutWidth) {
        final Boolean[] check = {false};
        return IntStream.range(0, chromosome.size()).filter(geneIndex -> chromosome.get(geneIndex).equals(stockAnotherIndex))
                .reduce(0, (a, b) -> {
                    if (!check[0]) {
                        check[0] = true;
                        return a + orders.get(b);
                    } else {
                        return a + orders.get(b) + cutWidth;
                    }
                });
    }

    public Population mutatePopulation(Population population) {
        System.out.println("===========IN THE PROCESS OF MUTATION===========");
        // Initialize new population
        Population newPopulation = new Population(population);

        int maxRandomInt = this.populationSize - 1;
        // Loop over current population by fitness
        int mutationSize = (int) (this.populationSize * this.mutationRate);
        for (int i = 0; i < mutationSize; ++i) {
            Integer worstPosition = findWorstPositionInPopulation(newPopulation, newPopulation.getIndividuals(), this.populationSize);
            Individual worstIndividual = newPopulation.getIndividual(worstPosition);
            double worstFitness = worstIndividual.getFitness();

            int randInt = getRandomNumber(this.elitismCount + 1, maxRandomInt);
            Individual randIndividual = newPopulation.getIndividual(randInt);
//            Individual individualSpecial = mutateSpecial(newPopulation, randIndividual);
            Individual individualSpecial = mutateIndividualV2(newPopulation, randIndividual);
            mutateV2(newPopulation, randIndividual.getChromosome(), worstPosition, worstFitness, randIndividual.getFitness(), individualSpecial);
//            mutate(newPopulation, randIndividual.getChromosome(), worstPosition, worstFitness, randIndividual.getFitness());
        }

        // Return mutated population
        return newPopulation;
    }

    private Individual mutateIndividualV2(Population newPopulation, Individual individualInit) {
        List<Integer> chromosome = new ArrayList<>(individualInit.getChromosome());

        List<Integer> orders = newPopulation.getOrders();
        List<Integer> stocks = newPopulation.getStocks();

        double fitnessBefore = individualInit.getFitness();

        int minOrder = orders.stream().min(Integer::compare).orElse(0);

        final List<Integer>[] stocksRemain = new List[]{newPopulation.getStockRemain(chromosome, stocks, orders)};

        Set<Integer> uniqueStocksInit = IntStream.range(0, stocks.size()).boxed().collect(Collectors.toSet());
        Set<Integer> uniqueStocks = new HashSet<>(chromosome);

        for (Integer stockIndex : uniqueStocksInit) {
//            if (this.mutationRate > Math.random()) {
                final Integer[] remainValue = {stocksRemain[0].get(stockIndex)};
                if (remainValue[0] >= minOrder) {
                    Set<Integer> uniqueStocksAnother = uniqueStocks.stream().filter(item -> !item.equals(stockIndex)).collect(Collectors.toSet());
                    for (Integer stockAnotherIndex : uniqueStocksAnother) {
                        int sumCurrentOrder = getTotalOrderByStockIndex(stockAnotherIndex, chromosome, orders, newPopulation.getCutWidth());

                        if (remainValue[0] >= sumCurrentOrder) {
                            chromosome.replaceAll(gene -> gene.equals(stockAnotherIndex) ? stockIndex : gene);
                            remainValue[0] -= sumCurrentOrder;
//                            uniqueStocks.remove(stockAnotherIndex);
                        }
                    }
                    stocksRemain[0] = newPopulation.getStockRemain(chromosome, stocks, orders);
                }
//            }
        }
        double fitnessAfter = newPopulation.getFitnessOfChromosome(chromosome, stocks, orders);
        if(fitnessAfter < fitnessBefore) {
            System.out.println(" mutateIndividualV2 success!");
            System.out.println("fitnessBefore: " + fitnessBefore);
            System.out.println("fitnessAfter: " + fitnessAfter);
            Individual individual = new Individual(chromosome);
            individual.setFitness(fitnessAfter);
            return individual;
        }
        return individualInit;
    }


    Integer findWorstPositionInPopulation(Population newPopulation, List<Individual> individuals, int populationSize) {
        List<Integer> orders = newPopulation.getOrders();
        List<Integer> stocks = newPopulation.getStocks();

        double worstValue = 1;
        int worstARNPosition = 0;

        for (int i = 0; i < populationSize; ++i) {
            double currValue = newPopulation.getFitnessOfChromosome(individuals.get(i).getChromosome(), stocks, orders);
            if (currValue > worstValue) {
                worstARNPosition = i;
                worstValue = currValue;
            }
        }

        return worstARNPosition;
    }

    void mutate(Population newPopulation, List<Integer> chromosome, int worstPosition, double worstValue, double currentFitness) {
        List<Integer> orders = newPopulation.getOrders();
        List<Integer> stocks = newPopulation.getStocks();

        List<Integer> stockTemp = newPopulation.getStockRemain(chromosome, stocks, orders);
        double bestGapOfAll = 1;
        int finalMoveTo = -1;
        int finalFromPosition = -1;

        for (int i = 0; i < orders.size(); ++i) {
            double bestGap = 1;
            int moveTo = -1;
            int fromPosition = -1;
            for (int j = 0; j < stocks.size(); ++j) {
                if (j == chromosome.get(i)) {
                    continue;
                }
                if (stockTemp.get(j) >= orders.get(i)) {
                    List<Integer> chromosomeTemp = new ArrayList<>(chromosome);
                    chromosomeTemp.set(i, j);
                    double rateRemain = newPopulation.getFitnessOfChromosome(chromosomeTemp, stocks, orders);
                    if (stockTemp.get(j).equals(orders.get(i)) && bestGap > rateRemain) {
                        bestGap = rateRemain;
                        moveTo = j;
                        fromPosition = i;
                    }

                    int cutWidth = newPopulation.getCutWidth();
                    if (stockTemp.get(j) >= orders.get(i) + cutWidth && bestGap > rateRemain) {
                        bestGap = rateRemain;
                        moveTo = j;
                        fromPosition = i;
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

        chromosome.set(finalFromPosition, finalMoveTo);

        // mutant individual
        Individual mutant = new Individual(chromosome);
        mutant.setFitness(newPopulation.getFitnessOfChromosome(chromosome, stocks, orders));

        if (mutant.getFitness() < worstValue && mutant.getFitness() < currentFitness) {
            newPopulation.getIndividuals().set(worstPosition, mutant);
        }
    }

    /**
     * @param newPopulation     newPopulation
     * @param chromosome        chromosome
     * @param worstPosition     worstPosition
     * @param worstValue        worstValue
     * @param currentFitness    currentFitness
     * @param individualSpecial
     */
    void mutateV2(Population newPopulation, List<Integer> chromosome, int worstPosition, double worstValue, double currentFitness, Individual individualSpecial) {
        List<Integer> orders = newPopulation.getOrders();
        List<Integer> stocks = newPopulation.getStocks();

        List<Integer> stockTemp = newPopulation.getStockRemain(chromosome, stocks, orders);

        Map<Integer, Integer> storeChangePosition = new HashMap<>();

        for (int i = 0; i < orders.size(); ++i) {
            double bestGap = Integer.MAX_VALUE;
            int moveTo = -1;
            int fromPosition = -1;
            for (int j = 0; j < stockTemp.size(); ++j) {
                if (j == chromosome.get(i)) {
                    continue;
                }
                if (stockTemp.get(j) >= orders.get(i)) {
                    List<Integer> chromosomeTemp = new ArrayList<>(chromosome);
                    chromosomeTemp.set(i, j);
                    double rateRemain = newPopulation.getFitnessOfChromosome(chromosomeTemp, stocks, orders);
                    if (stockTemp.get(j).equals(orders.get(i)) && bestGap > rateRemain) {
                        bestGap = rateRemain;
                        moveTo = j;
                        fromPosition = i;
                    }

                    int cutWidth = newPopulation.getCutWidth();
                    if (stockTemp.get(j) >= orders.get(i) + cutWidth && bestGap > rateRemain) {
                        bestGap = rateRemain;
                        moveTo = j;
                        fromPosition = i;
                    }
                }
            }
            if (fromPosition > 0 && moveTo > 0) {
                List<Integer> chromosomeTemp = new ArrayList<>(chromosome);
                chromosomeTemp.set(fromPosition, moveTo);
                stockTemp = newPopulation.getStockRemain(chromosomeTemp, stocks, orders);
                storeChangePosition.put(fromPosition, moveTo);
            }
        }

        Individual individualFinal = null;
        if (!storeChangePosition.isEmpty()) {
            List<Integer> newChromosome = changeChromosome(chromosome, storeChangePosition);
            // mutant individual
            Individual mutant = new Individual(newChromosome);
            mutant.setFitness(newPopulation.getFitnessOfChromosome(newChromosome, stocks, orders));

            if (mutant.getFitness() < worstValue && mutant.getFitness() < currentFitness) {
                if (mutant.getFitness() < individualSpecial.getFitness()) {
                    individualFinal = mutant;
                }
            }
        }

        if (individualSpecial.getFitness() < currentFitness) {
            newPopulation.getIndividuals().set(worstPosition, individualSpecial);
        }

        if (null != individualFinal) {
            newPopulation.getIndividuals().set(worstPosition, individualFinal);
        }
    }

    /**
     *
     */
    Individual mutateSpecial(Population newPopulation, Individual individualInit) {

        List<Integer> chromosome = individualInit.getChromosome();
        double currentFitness = individualInit.getFitness();

        List<Integer> orders = newPopulation.getOrders();
        List<Integer> stocks = newPopulation.getStocks();

        List<Integer> uniqueStocks = chromosome.stream().distinct().sorted().collect(Collectors.toList());

        List<Individual> individuals = new ArrayList<>();

        uniqueStocks.forEach(geneIndexSpecial -> {
            List<Integer> chromosomeTemp = new ArrayList<>(chromosome);
            List<Integer> stockTemp = newPopulation.getStockRemain(chromosomeTemp, stocks, orders);

            for (int geneIndex = 0; geneIndex < chromosomeTemp.size(); geneIndex++) {

                if (stockTemp.get(geneIndexSpecial) >= orders.get(geneIndex) && geneIndex != geneIndexSpecial) {

                    if (stockTemp.get(geneIndexSpecial).equals(orders.get(geneIndex))) {
                        chromosomeTemp.set(geneIndex, geneIndexSpecial);
                        stockTemp = newPopulation.getStockRemain(chromosomeTemp, stocks, orders);
                    }

                    int cutWidth = newPopulation.getCutWidth();
                    if (stockTemp.get(geneIndexSpecial) >= orders.get(geneIndex) + cutWidth) {
                        chromosomeTemp.set(geneIndex, geneIndexSpecial);
                        stockTemp = newPopulation.getStockRemain(chromosomeTemp, stocks, orders);
                    }
                }
            }
            Individual individual = new Individual(chromosomeTemp);
            double fitness = newPopulation.getFitnessOfChromosome(chromosomeTemp, stocks, orders);
            individual.setFitness(fitness);
            individuals.add(individual);
        });

        individuals.sort(Comparator.comparing(Individual::getFitness));
        if (!individuals.isEmpty() && individuals.get(0).getFitness() < currentFitness) {
            return individuals.get(0);
        }
        return individualInit;
    }

    private List<Integer> changeChromosome(List<Integer> chromosome, Map<Integer, Integer> storeChangePosition) {
        List<Integer> chromosomeTemp = new ArrayList<>(chromosome);
        storeChangePosition.forEach(chromosomeTemp::set);
        return chromosomeTemp;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }
}
