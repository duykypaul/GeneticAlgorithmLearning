package com.duykypaul.steel;

import com.duykypaul.chapter2.GeneticAlgorithm;
import com.duykypaul.chapter2.Population;

import java.time.Duration;
import java.time.Instant;

public class ARNV2 {
    public static void main(String[] args) {

        Instant start = Instant.now();
        // create GA Object
        GeneticAlgorithm ga = new GeneticAlgorithm(50, 0.001, 0.95, 10);

        // Initialize population
        Population population = ga.initPopulation(50);

        ga.evalPopulation(population);
        int generation = 1;
        while (!ga.isTerminationConditionMet(population) && Duration.between(start, Instant.now()).getSeconds() <= 100) {
            // Print fittest individual from population
            System.out.println("Best solution: " + population.getFittest(0).toString());

            // Apply crossover
            population = ga.crossoverPopulation(population);

            // Apply mutation
            population = ga.mutatePopulation(population);

            // Evaluate population
            ga.evalPopulation(population);

            // Increment the current generation
            generation++;
        }

        System.out.println("Found solution in " + generation + " generations");
        System.out.println("Best solution: " + population.getFittest(0).toString());

        Instant end = Instant.now();
        System.out.println(Duration.between(start, end));
    }
}
