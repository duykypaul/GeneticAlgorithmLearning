package com.duykypaul.arn;

import com.duykypaul.kltn.FastCutBean;
import org.javatuples.Triplet;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.duykypaul.arn.SaveCut.COMMA;


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
        if(stockState.stream().distinct().count() == 1) {
            generatePopulationApFast(stocksInit, ordersInit, cutWidth, generationLimit);
        } else {
            generatePopulation(chromosome, stockState, populationSize);
        }


        if (this.realPopulationSize > populationSize) {
            this.population = this.population.subList(0, populationSize);
            this.realPopulationSize = populationSize;
        }
    }

    private void generatePopulationApFast(List<Integer> stocksInit, List<Integer> ordersInit, int cutWidth, int generationLimit) {
        List<Individual> listArn = new ArrayList<>();
        int[] messageFromArnAlgorithm;
        /*
         * arrOrderInit = 2000, 3000, 5000, 1500, 7000
         */
        int[] arrOrderInit = ordersInit.stream().mapToInt(Integer::intValue).toArray();

        /*
         * sắp xếp mảng order theo thứ tự từ cao đến thấp
         * arrOrder = 7000, 5000, 3000, 2000, 1500
         */
        int[] arrOrder = IntStream.of(arrOrderInit)
            .boxed()
            .sorted(Comparator.reverseOrder())
            .mapToInt(i -> i)
            .toArray();

        /*
         * sắp xếp lại chỉ số index trong mảng order ban đầu theo thứ tự từ cao đến thấp (của giá trị)
         * sortedIndicesOrder = 4, 2, 1, 0, 3
         */
        int[] sortedIndicesOrder = IntStream.range(0, arrOrderInit.length)
            .boxed()
            .sorted(Comparator.comparing(i -> -arrOrderInit[i]))
            .mapToInt(ele -> ele)
            .toArray();

        int[] arrStockInit = stocksInit.stream().mapToInt(Integer::intValue).toArray();

        /*
         * sắp xếp mảng stock theo thứ tự từ cao đến thấp
         */
        int[] arrStock = IntStream.of(arrStockInit)
            .boxed().sorted(Comparator.reverseOrder())
            .mapToInt(Integer::intValue)
            .toArray();

        /*
         * chỉ số index trong mảng stock theo thứ tự từ cao đến thấp
         */
        int[] sortedIndicesStock = IntStream.range(0, arrStockInit.length)
            .boxed()
            .sorted(Comparator.comparing(i -> -arrStockInit[i]))
            .mapToInt(Integer::intValue)
            .toArray();

        int[] arrCheckMaterialCanBeCut = new int[arrStock.length];
        int numberMaterialRemoved = 0;
        Arrays.fill(arrCheckMaterialCanBeCut, 1);

        findSolution(listArn, arrCheckMaterialCanBeCut, numberMaterialRemoved, arrStock, arrOrder, cutWidth);

        if (!listArn.isEmpty()) {
            listArn = listArn.stream()
                .sorted(Comparator.comparing(Individual::getFitness))
                .collect(Collectors.toList());

            for (Individual individualCurrent : listArn) {
                messageFromArnAlgorithm = individualCurrent.getChromosome().stream().mapToInt(Integer::intValue).toArray();
                Map<Integer, Integer> mapOutputArn = new HashMap<>();
                Map<Integer, Integer> mapIndexSortedIndicesOrder = new HashMap<>();
                for (int i = 0; i < arrOrder.length; i++) {
                    mapOutputArn.put(i, messageFromArnAlgorithm[i]);
                    mapIndexSortedIndicesOrder.put(sortedIndicesOrder[i], i);
                }
                int[] arrMessageConvertByOrder = new int[arrOrder.length];
                for (int i = 0; i < arrOrder.length; i++) {
                    arrMessageConvertByOrder[i] = mapOutputArn.get(mapIndexSortedIndicesOrder.get(i));
                }
                int[] arrMessageConvertForStock = new int[arrMessageConvertByOrder.length];
                for (int i = 0; i < arrMessageConvertByOrder.length; i++) {
                    arrMessageConvertForStock[i] = sortedIndicesStock[arrMessageConvertByOrder[i]];
                }
                individualCurrent.setChromosome(IntStream.of(arrMessageConvertForStock).boxed().collect(Collectors.toList()));
                this.population.add(individualCurrent);
                this.realPopulationSize++;
            }
        }
    }

    private void findSolution(List<Individual> listArn, int[] arrCheckMaterialCanBeCut, int numberMaterialRemoved, int[] arrStock, int[] arrOrder, int cutWidth) {

        while (true) {
            int[] arrRemain = new int[arrStock.length];
            OptionalInt minOder = Arrays.stream(arrOrder).min();
            /*
             * mảng lưu cách cắt toriai
             */
            int[] arrIndexStockUsed = new int[arrOrder.length];
            Arrays.fill(arrIndexStockUsed, -1);
            for (int i = 0; i < arrStock.length; i++) {
                if (arrCheckMaterialCanBeCut[i] == 1) {
                    int remaining = arrStock[i];
                    for (int j = 0; j < arrOrder.length; j++) {
                        if (remaining < minOder.getAsInt()) break;
                        if (remaining >= arrOrder[j] && arrIndexStockUsed[j] == -1) {
                            arrIndexStockUsed[j] = i;
                            remaining -= (arrOrder[j] + cutWidth);
                            if (remaining < 0) {
                                remaining = 0;
                            }
                        }
                    }
                    arrRemain[i] = remaining;
                }
            }
            /*
             * kiểm tra nếu không thể cắt được nữa thì dừng lại
             */
            if (Arrays.stream(arrIndexStockUsed).anyMatch(item -> item == -1)) {
                break;
            }
            /*
             * số lượng thanh sắt cần dùng
             */

            Supplier<IntStream> otherRemainStock = () -> IntStream.range(0, arrRemain.length)
                .filter(i -> arrRemain[i] != arrStock[i] && Arrays.stream(arrIndexStockUsed).anyMatch(index -> index == i));
            /*
             * tổng phần thừa với cách cắt tương ứng
             */
            int remain = otherRemainStock.get().mapToObj(i -> arrRemain[i]).mapToInt(Integer::intValue).sum();
            int stockUsed = otherRemainStock.get().mapToObj(i -> arrStock[i]).mapToInt(Integer::intValue).sum();

            double rateRemain = (double) remain / stockUsed;
            Individual individual = new Individual(Arrays.stream(arrIndexStockUsed).boxed().collect(Collectors.toList()));
            individual.setFitness(rateRemain);
            listArn.add(individual);
            arrCheckMaterialCanBeCut[numberMaterialRemoved++] = 0;
            findSolution(listArn, arrCheckMaterialCanBeCut, numberMaterialRemoved, arrStock, arrOrder, cutWidth);
        }
    }

    public double getFitnessOfChromosome(List<Integer> currentChromosome, List<Integer> stocks, List<Integer> orders) {
        List<Integer> uniqueStocks = currentChromosome.stream().distinct().sorted().collect(Collectors.toList());
        List<Integer> stockTemp = getStockRemain(currentChromosome, stocks, orders);

        boolean isContainNegative = stockTemp.stream().parallel().anyMatch(item -> item < 0);
        if (isContainNegative) {
//            System.out.println("Negative contain");
            return 1;
        }


        int StockUsed = IntStream.range(0, stocks.size()).filter(uniqueStocks::contains).map(stocks::get).sum();
        int StockRemain = IntStream.range(0, stocks.size()).filter(uniqueStocks::contains).map(stockTemp::get).sum();

        return (double) StockRemain / StockUsed;
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

    void generatePopulation(List<Integer> currentChromosome, List<Integer> stockState, int populationSize) {
        try {
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
                double rate = getFitnessOfChromosome(currentChromosome, stocks, orders);
                individual.setFitness(rate);
                this.population.add(individual);
                this.realPopulationSize++;
                if(this.realPopulationSize >= populationSize) {
                    isFinishedGen = true;
                }
                return;
            }
            // Next Node
            for (int stockIndex = 0; stockIndex < stockState.size(); stockIndex++) {
                duration = Duration.between(startGen, Instant.now()).getSeconds();
                if (duration > this.generationLimit) {
                    isFinishedGen = true;
                    return;
                }

                int idx = currentChromosome.size();
                if (orders.get(idx).equals(stockState.get(stockIndex))) {
                    List<Integer> stocksTemp = new ArrayList<>(stockState);
                    // update stocks
                    stocksTemp.set(stockIndex, stocksTemp.get(stockIndex) - orders.get(idx));

                    currentChromosome.add(stockIndex);

                    generatePopulation(currentChromosome, stocksTemp, populationSize);
                    currentChromosome.remove(currentChromosome.size() - 1);

                    duration = Duration.between(startGen, Instant.now()).getSeconds();
                    if (duration > this.generationLimit) {
                        isFinishedGen = true;
                        return;
                    }
                } else {

                    if (orders.get(idx) + this.cutWidth <= stockState.get(stockIndex)) {
                        List<Integer> stocksTemp = new ArrayList<>(stockState);
                        // update stocks state
                        stocksTemp.set(stockIndex, stocksTemp.get(stockIndex) - orders.get(idx) - this.cutWidth);

                        // add to ARN
                        currentChromosome.add(stockIndex);
                        System.out.println("stockIndex:" + stockIndex);
                        System.out.println("idx:" + idx);
                        generatePopulation(currentChromosome, stocksTemp, populationSize);
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
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
