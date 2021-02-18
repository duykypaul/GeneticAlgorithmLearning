package com.duykypaul;

import javafx.util.Pair;
import org.javatuples.Triplet;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ARN {
    public static final int POPULATION_LIMIT = 5000;
    public static final int AMOUNT_OF_SECOND = 3;
    public static final int AMOUNT_OF_SECOND_GEN = 1;

    public static final long TIME_LIMIT = AMOUNT_OF_SECOND;
    public static final long GEN_LIMIT = AMOUNT_OF_SECOND_GEN;
    public static final float MUTATION_RATE = 0.05f;

    private boolean isFinishedGen = false;
    private Instant start_gen = Instant.now();
    private int stocksN = 0;
    private int ordersN = 0;
    private int CUT_WIDTH = 0;
    private int ARNsN = 0;
    private List<Integer> stocks = new ArrayList<>();
    private List<Integer> orders = new ArrayList<>();
    private List<List<Integer>> ARNs = new ArrayList<>();

    public static void main(String[] args) {
        String inputMessage = "7000,5000,13000,13000,6000|5000,5000,5000,10000|5";
        String inputMessage1 = "2220,2220,2534,7093,7093,13000,13000,13000,13000,13000,13000,13000,13000,13000,13000,13000,13000,13000,13000,13000|3303,3303,3303,3303,3303,3303,3180,3180,3180,4080,4080,4080,4080,4180,4180,990|5";
        ARN arn = new ARN();
        Instant start = Instant.now();
        System.out.println(arn.algorithm_task(inputMessage1));
        System.out.println("time line: " + Duration.between(start, Instant.now()));
    }

    private static String genStringNoFromSize(int n) {
        StringBuilder returnVal = new StringBuilder();
        for (int i = 0; i < n; i++) {
            returnVal.append("0");
        }
        return returnVal.toString();
    }

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    String algorithm_task(String inputContent) {

        List<String> parts = Arrays.asList(inputContent.split("\\|").clone());

        stocks = Arrays.stream(parts.get(0).split(",").clone()).map(Integer::parseInt).collect(Collectors.toList());
        orders = Arrays.stream(parts.get(1).split(",").clone()).map(Integer::parseInt).collect(Collectors.toList());
        CUT_WIDTH = Integer.parseInt(parts.get(2));

        System.out.println("sum stocks: " + stocks.stream().reduce(0, Integer::sum));
        System.out.println("sum orders: " + orders.stream().reduce(0, Integer::sum));

        stocksN = stocks.size();
        ordersN = orders.size();

        Pair<Integer, Integer> chromosomeSimulateVal = chromosomeSimulate();

        int bestValue = chromosomeSimulateVal.getKey(),
            bestPosition = chromosomeSimulateVal.getValue();

        if (bestValue < 0 && bestPosition < 0) {
            return "0";
        }

        System.out.println("Best Value = " + bestValue);
        String content = ARNs.get(bestPosition).stream()
            .map(String::valueOf)
            .collect(Collectors.joining(","));
        System.out.print("Best ARN = " + content);
        System.out.println();

        content = genStringNoFromSize(6 - String.valueOf(content.length()).length())
            + content.length() + content + "|";

        return content;
    }

    public int getWeightOfARN(List<Integer> ARN) {
        List<Integer> uniqueStocks = ARN.stream().distinct().sorted().collect(Collectors.toList());
        List<Integer> stockTemp = computeStockRemain(ARN);

        int value = 0;
        for (Integer uniqueStock : uniqueStocks) {
            value += stockTemp.get(uniqueStock);
        }

        return value;
    }

    public Pair<Integer, Integer> chromosomeSimulate() {
        List<Integer> ARN = new ArrayList<>();
        List<Integer> stockState = new ArrayList<>(stocks);

        start_gen = Instant.now();
        isFinishedGen = false;
        generateARN(ARN, stockState);

        System.out.println(105);
//        ARNs.forEach(item -> System.out.println(Arrays.toString(item.toArray())));

        ARNs.sort(Comparator.comparingInt(this::getWeightOfARN));

        if (ARNsN > POPULATION_LIMIT) {
            ARNs = ARNs.subList(0, POPULATION_LIMIT);
            ARNsN = POPULATION_LIMIT;
        }

        System.out.println("POPULATION = " + ARNsN);

        if (ARNsN == 0) {
            return new Pair<>(-1, -1);
        }

        int maxRandomInt = ARNsN - 1;

        Instant start = Instant.now(); // auto start = high_resolution_clock::now();
        int bestPosition = 0;

        int genCount = 0;
        while (true) {
            genCount++;
            int maxARNPosition = 0, secondARNPosition, worstARNPosition;
            int mutationARNsN = (int) (ARNsN * MUTATION_RATE);
            //todo thinking mutationARNsN
            for (int i = 0; i < mutationARNsN; ++i) {
                Triplet<Integer, Integer, Integer> findBestInPopulationVal = findBestInPopulation(ARNs, ARNsN);
                maxARNPosition = findBestInPopulationVal.getValue0();
                secondARNPosition = findBestInPopulationVal.getValue1();
                worstARNPosition = findBestInPopulationVal.getValue2();
                int randInt = getRandomNumber(secondARNPosition + 1, maxRandomInt);
                mutate(ARNs.get(randInt), worstARNPosition, getWeightOfARN(ARNs.get(worstARNPosition)));
            }
            System.out.println("Best Value" + genCount + " = " + getWeightOfARN(ARNs.get(maxARNPosition)));

            Instant stop = Instant.now();
            long duration = Duration.between(start, stop).getSeconds();
            if (duration > TIME_LIMIT) {
                bestPosition = maxARNPosition;
                break;
            }
        }
        System.out.println("In " + genCount + " Generations");
        return new Pair<>(getWeightOfARN(ARNs.get(bestPosition)), bestPosition);
    }

    Triplet<Integer, Integer, Integer> findBestInPopulation(List<List<Integer>> population, int populationSize) {
        int maxValue = Integer.MAX_VALUE;
        int maxARNPosition = 0;
        
        int secondValue = Integer.MAX_VALUE;
        int secondARNPosition = 0;

        int worstValue = Integer.MIN_VALUE;
        int worstARNPosition = 0;

        for (int i = 0; i < populationSize; ++i) {
            int currValue = getWeightOfARN(population.get(i));
            if (currValue < maxValue) {
                secondValue = maxValue;
                secondARNPosition = maxARNPosition;
                maxValue = currValue;
                maxARNPosition = i;
            } else {
                if (secondValue > currValue) {
                    secondValue = currValue;
                    secondARNPosition = i;
                }
            }

            if (currValue > worstValue) {
                worstARNPosition = i;
                worstValue = currValue;
            }
        }

        return new Triplet<>(maxARNPosition, secondARNPosition, worstARNPosition);
    }

    void generateARN(List<Integer> currARN, List<Integer> currStockState) {
        // Population limit reached!
        if (isFinishedGen) {
            return;
        }
        long duration = Duration.between(start_gen, Instant.now()).getSeconds();
        if (duration > GEN_LIMIT) {
            isFinishedGen = true;
            return;
        }

        // Get a complete ARN
        if (currARN.size() == ordersN) {
            ARNs.add(new ArrayList<>(currARN));
            ARNsN++;
            return;
        }
        // Next Node
        for (int i = 0; i < stocksN; i++) {
            if (isFinishedGen) {
                return;
            }
            duration = Duration.between(start_gen, Instant.now()).getSeconds();
            if (duration > GEN_LIMIT) {
                isFinishedGen = true;
                return;
            }

            int idx = currARN.size();

            if (orders.get(idx).equals(currStockState.get(i))) {
                List<Integer> stockTemp = new ArrayList<>(currStockState);
                stockTemp.set(i, stockTemp.get(i) - orders.get(idx));
                currARN.add(i);
                generateARN(currARN, stockTemp);
                currARN.remove(currARN.size() - 1);
                if (isFinishedGen) {
                    return;
                }
                duration = Duration.between(start_gen, Instant.now()).getSeconds();
                if (duration > GEN_LIMIT) {
                    isFinishedGen = true;
                    return;
                }
            } else {
                if (orders.get(idx) + CUT_WIDTH <= currStockState.get(i)) {
                    List<Integer> stockTemp = new ArrayList<>(currStockState);
                    stockTemp.set(i, stockTemp.get(i) - orders.get(idx) - CUT_WIDTH);
                    currARN.add(i);
                    generateARN(currARN, stockTemp);
                    currARN.remove(currARN.size() - 1);
                    if (isFinishedGen) {
                        return;
                    }
                    duration = Duration.between(start_gen, Instant.now()).getSeconds();
                    if (duration > GEN_LIMIT) {
                        isFinishedGen = true;
                        return;
                    }
                }
            }

            duration = Duration.between(start_gen, Instant.now()).getSeconds();
            if (duration > GEN_LIMIT) {
                isFinishedGen = true;
                return;
            }
        }
    }

    void mutate(List<Integer> ARN, int worstPosition, int worstValue) {
        List<Integer> stockTemp = computeStockRemain(ARN);
        //todo thinking bestGapOfAll for what?
        int bestGapOfAll = Integer.MAX_VALUE;
        //todo thinking finalMoveTo for what?
        int finalMoveTo = -1;
        //todo thinking finalFromPosition for what?
        int finalFromPosition = -1;

        for (int i = 0; i < ordersN; ++i) {
            int bestGap = Integer.MAX_VALUE;
            int moveTo = -1;
            int fromPosition = -1;
            for (int j = 0; j < stocksN; ++j) {
                if (j == ARN.get(i)) {
                    continue;
                }
                if (stockTemp.get(j) >= orders.get(i)) {
                    if (stockTemp.get(j).equals(orders.get(i))) {
                        if (bestGap > -stockTemp.get(ARN.get(i))) {
                            bestGap = -stockTemp.get(ARN.get(i));
                            moveTo = j;
                            fromPosition = i;
                        }
                    }
                    if (stockTemp.get(j) >= orders.get(i) + CUT_WIDTH) {
                        int number = (stockTemp.get(j) - (orders.get(i) + CUT_WIDTH)) - stockTemp.get(ARN.get(i));
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

        if (getWeightOfARN(ARN) < worstValue) {
            ARNs.set(worstPosition, ARN);
        }
    }

    private List<Integer> computeStockRemain(List<Integer> ARN) {
        List<Integer> stockTemp = new ArrayList<>(stocks);
        for (int i = 0; i < ordersN; ++i) {
            if (stockTemp.get(ARN.get(i)).equals(orders.get(i))) {
                stockTemp.set(ARN.get(i), stockTemp.get(ARN.get(i)) - orders.get(i));
            } else {
                stockTemp.set(ARN.get(i), stockTemp.get(ARN.get(i)) - (orders.get(i) + CUT_WIDTH));
            }
        }
        return stockTemp;
    }
}
