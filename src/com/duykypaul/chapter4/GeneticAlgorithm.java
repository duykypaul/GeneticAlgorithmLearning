package com.duykypaul.chapter4;

import com.duykypaul.chapter3.Maze;
import com.duykypaul.chapter3.Robot;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

/**
 * Please see chapter2/GeneticAlgorithm for additional comments.
 * 
 * This GeneticAlgorithm class is designed to solve the
 * "Robot Controller in a Maze" problem, and is necessarily a little different
 * from the chapter2/GeneticAlgorithm class.
 * 
 * This class introduces the concepts of tournament selection and single-point
 * crossover. Additionally, the calcFitness method is vastly different from the
 * AllOnesGA fitness method; in this case we actually have to evaluate how good
 * the robot is at navigating a maze!
 * 
 * @author bkanber
 *
 */
public class GeneticAlgorithm {

	/**
	 * See chapter2/GeneticAlgorithm for a description of these properties.
	 */
	private int populationSize;
	private double mutationRate;
	private double crossoverRate;
	private int elitismCount;

	/**
	 * A new property we've introduced is the size of the population used for
	 * tournament selection in crossover.
	 */
	protected int tournamentSize;

	public GeneticAlgorithm(int populationSize, double mutationRate, double crossoverRate, int elitismCount,
			int tournamentSize) {

		this.populationSize = populationSize;
		this.mutationRate = mutationRate;
		this.crossoverRate = crossoverRate;
		this.elitismCount = elitismCount;
		this.tournamentSize = tournamentSize;
	}

	/**
	 * Initialize population
	 * 
	 * @param chromosomeLength
	 *            The length of the individuals chromosome
	 * @return population The initial population generated
	 */
	public Population initPopulation(int chromosomeLength) {
		// Initialize population
		return new Population(this.populationSize, chromosomeLength);
	}

	/**
	 * Calculate fitness for an individual.
	 * 
	 * This fitness calculation is a little more involved than chapter2's. In
	 * this case we initialize a new Robot class, and evaluate its performance
	 * in the given maze.
	 * 
	 * @param individual
	 *            the individual to evaluate
	 * @param cities
	 *            the cities being referenced
	 * @return double The fitness value for individual
	 */
	public double calcFitness(Individual individual, City[] cities) {
		// get fitness
		Route route = new Route(individual, cities);

		//a shorter distance therefore has a higher score.
		double fitness = 1 / route.getDistance();

		// store fitness
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
	 * The difference between this method and the one in chapter2 is that this
	 * method requires the maze itself as a parameter; unlike the All Ones
	 * problem in chapter2, we can't determine a fitness just by looking at the
	 * chromosome -- we need to evaluate each member against the maze.
	 * 
	 * @param population
	 *            the population to evaluate
	 * @param cities
	 *            the maze to evaluate each individual against.
	 */
	public void evalPopulation(Population population, City[] cities) {
		double populationFitness = 0;

		// Loop over population evaluating individuals and summing population fitness
		for (Individual individual : population.getIndividuals()) {
			populationFitness += this.calcFitness(individual, cities);
		}

		double avgFitness = populationFitness / population.size();
		population.setPopulationFitness(avgFitness);
	}

	/**
	 * Check if population has met termination condition
	 * 
	 * We don't actually know what a perfect solution looks like for the robot
	 * controller problem, so the only constraint we can give to the genetic
	 * algorithm is an upper bound on the number of generations.
	 * 
	 * @param generationsCount
	 *            Number of generations passed
	 * @param maxGenerations
	 *            Number of generations to terminate after
	 * @param start
	 * @param timeout
	 * @return boolean True if termination condition met, otherwise, false
	 */
	public boolean isTerminationConditionMet(int generationsCount, int maxGenerations, Instant start, int timeout) {
		return generationsCount > maxGenerations || Duration.between(start, Instant.now()).getSeconds() >= timeout;
	}

	/**
	 * Selects parent for crossover using tournament selection
	 * 
	 * Tournament selection works by choosing N random individuals, and then
	 * choosing the best of those.
	 * 
	 * @param population
	 * @return The individual selected as a parent
	 */
	public Individual selectParent(Population population) {
		// Create tournament
		Population tournament = new Population(this.tournamentSize);

		// Add random individuals to the tournament
		population.shuffle();
		for (int i = 0; i < this.tournamentSize; i++) {
			Individual tournamentIndividual = population.getIndividual(i);
			tournament.setIndividual(i, tournamentIndividual);
		}

		// Return the best
		return tournament.getFittest(0);
	}

	/**
	 * Apply mutation to population
	 * 
	 * This method is the same as chapter2's version.
	 * 
	 * @param population
	 *            The population to apply mutation to
	 * @return The mutated population
	 */
	public Population mutatePopulation(Population population) {
		// Initialize new population
		Population newPopulation = new Population(this.populationSize);

		// Loop over current population by fitness
		for (int populationIndex = 0; populationIndex < population.size(); populationIndex++) {
			Individual individual = population.getFittest(populationIndex);

			// Skip mutation if this is an elite individual
			if (populationIndex >= this.elitismCount) {
				// System.out.println("Mutating population member "+populationIndex);
				// Loop over individual's genes
				for (int geneIndex = 0; geneIndex < individual.getChromosomeLength(); geneIndex++) {
					// System.out.println("\tGene index "+geneIndex);
					// Does this gene need mutation?
					if (this.mutationRate > Math.random()) {
						// Get new gene position
						int newGenePos = (int) (Math.random() * individual.getChromosomeLength());
						// Get genes to swap
						int gene1 = individual.getGene(newGenePos);
						int gene2 = individual.getGene(geneIndex);
						// Swap genes
						individual.setGene(geneIndex, gene1);
						individual.setGene(newGenePos, gene2);
					}
				}
			}

			// Add individual to population
			newPopulation.setIndividual(populationIndex, individual);
		}

		// Return mutated population
		return newPopulation;
	}

	/**
	 * Crossover population using single point crossover
	 * 
	 * Single-point crossover differs from the crossover used in chapter2.
	 * Chapter2's version simply selects genes at random from each parent, but
	 * in this case we want to select a contiguous region of the chromosome from
	 * each parent.
	 * 
	 * For instance, chapter2's version would look like this:
	 * 
	 * Parent1: AAAAAAAAAA 
	 * Parent2: BBBBBBBBBB 
	 * Child  : AABBAABABA
	 * 
	 * This version, however, might look like this:
	 * 
	 * Parent1: AAAAAAAAAA 
	 * Parent2: BBBBBBBBBB 
	 * Child  : AAAABBBBBB
	 * 
	 * @param population
	 *            Population to crossover
	 * @return Population The new population
	 */
	public Population crossoverPopulation(Population population) {
		// Create new population
		Population newPopulation = new Population(population.size());

		// Loop over current population by fitness
		for (int populationIndex = 0; populationIndex < population.size(); populationIndex++) {
			// Get parent1
			Individual parent1 = population.getFittest(populationIndex);

			// Apply crossover to this individual?
			if (this.crossoverRate > Math.random() && populationIndex >= this.elitismCount) {
				// Find parent2 with tournament selection
				Individual parent2 = this.selectParent(population);

				// Create blank offspring chromosome
				int[] offspringChromosome = new int[parent1.getChromosomeLength()];
				Arrays.fill(offspringChromosome, -1);
				Individual offspring = new Individual(offspringChromosome);

				// Get subset of parent chromosomes
				int substrPos1 = (int) (Math.random() * parent1.getChromosomeLength());
				int substrPos2 = (int) (Math.random() * parent1.getChromosomeLength());

				// make the smaller the start and the larger the end
				final int startSubstr = Math.min(substrPos1, substrPos2);
				final int endSubstr = Math.max(substrPos1, substrPos2);

				// Loop and add the sub tour from parent1 to our child
				for (int i = startSubstr; i < endSubstr; i++) {
					offspring.setGene(i, parent1.getGene(i));
				}

				// Loop through parent2's city tour
				for (int i = 0; i < parent2.getChromosomeLength(); i++) {
					int parent2Gene = i + endSubstr;
					if (parent2Gene >= parent2.getChromosomeLength()) {
						parent2Gene -= parent2.getChromosomeLength();
					}

					// If offspring doesn't have the city add it
					if (!offspring.containsGene(parent2.getGene(parent2Gene))) {
						// Loop to find a spare position in the child's tour
						for (int ii = 0; ii < offspring.getChromosomeLength(); ii++) {
							// Spare position found, add city
							if (offspring.getGene(ii) == -1) {
								offspring.setGene(ii, parent2.getGene(parent2Gene));
								break;
							}
						}
					}
				}

				// Add child
				newPopulation.setIndividual(populationIndex, offspring);
			} else {
				// Add individual to new population without applying crossover
				newPopulation.setIndividual(populationIndex, parent1);
			}
		}

		return newPopulation;
	}
}
