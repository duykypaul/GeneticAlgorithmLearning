package com.duykypaul.kltn;

import java.util.List;
import java.util.stream.Collectors;

/**
 * An "Individual" represents a single candidate solution. The core piece of
 * information about an individual is its "chromosome", which is an encoding of
 * a possible solution to the problem at hand. A chromosome can be a string, an
 * array, a list, etc -- in this class, the chromosome is an integer array.
 *
 * An individual position in the chromosome is called a gene, and these are the
 * atomic pieces of the solution that can be manipulated or mutated. When the
 * chromosome is a string, as in this case, each character or set of characters
 * can be a gene.
 *
 * An individual also has a "fitness" score; this is a number that represents
 * how good a solution to the problem this individual is. The meaning of the
 * fitness score will vary based on the problem at hand.
 *
 * @author bkanber
 *
 */
public class Individual {
    private final List<Integer> chromosome;
    private final List<Integer> chromosomeMachine;
    private final List<String> chromosomeTime;
    private double fitness = -1;

    /**
     * Initializes individual with specific chromosome and chromosomeMachine
     *
     * @param chromosome        The chromosome to give individual
     * @param chromosomeMachine The chromosomeMachine to give individual
     */
    public Individual(List<Integer> chromosome, List<Integer> chromosomeMachine, List<String> chromosomeTime) {
        // Create individual chromosome
        this.chromosome = chromosome;
        this.chromosomeMachine = chromosomeMachine;
        this.chromosomeTime = chromosomeTime;
    }

    /**
     * Gets individual's chromosome
     *
     * @return The individual's chromosome
     */
    public List<Integer> getChromosome() {
        return this.chromosome;
    }

    /**
     * Gets individual's chromosomeMachine
     *
     * @return The individual's chromosomeMachine
     */
    public List<Integer> getChromosomeMachine() {
        return this.chromosomeMachine;
    }

    /**
     * Gets individual's chromosomeTime
     *
     * @return The individual's chromosomeTime
     */
    public List<String> getChromosomeTime() {
        return this.chromosomeTime;
    }

    /**
     * Gets individual's chromosome length
     *
     * @return The individual's chromosome length
     */
    public int getChromosomeLength() {
        return this.chromosome.size();
    }

    /**
     * Set gene at offset
     *
     * @param gene gene
     * @param offset offset
     */
    public void setGene(int offset, int gene) {
        this.chromosome.set(offset, gene);
    }

    /**
     * Get gene at offset
     *
     * @param offset offset
     * @return gene
     */
    public int getGene(int offset) {
        return this.chromosome.get(offset);
    }

    /**
     * Store individual's fitness
     *
     * @param fitness
     *            The individuals fitness
     */
    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    /**
     * Gets individual's fitness
     *
     * @return The individual's fitness
     */
    public double getFitness() {
        return this.fitness;
    }


    /**
     * Display the chromosome as a string.
     *
     * @return string representation of the chromosome
     */
    public String toString() {
        return this.chromosome.stream()
            .map(String::valueOf)
            .collect(Collectors.joining(","))
            .concat("|")
            .concat(this.chromosomeMachine.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")));
    }
}
