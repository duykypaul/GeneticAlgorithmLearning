package com.duykypaul.kltn;

import org.javatuples.Triplet;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AFast {

    private static final String BLANK = "";
    private static final String COMMA = ",";
    private static final String STEEL_BLADE_THICKNESS = "5";

    public static void main(String[] args) {
        Instant start = Instant.now();
//        testCase1();
//        testCase2();
//        testCase3();
//        testCase4();
        /*String order = "1250,1250,1250,1200,1200,1000,1000,1000,1000";
        String stock = "5623,1009,1640,1640,13000,13000,13000";*/
        String order = "1250,1250,1250,115,115,122,122,122,122,122,122,122,122,122,122";
        String stock = "1313,910,1188,2185,2545,2545,2900,2900,3285,4329,4329,4329,8594,1025";
        System.out.println(getMessageFromFastCut(order, stock));
        Instant end = Instant.now();
        System.out.println(Duration.between(start, end));
    }

    public static void testCase1() {
        List<Stack> listStack = new ArrayList<>();
        listStack.add(new Stack(1, 1, 10, 3000, LocalDate.parse("2021-02-28")));
        listStack.add(new Stack(1, 2, 20, 2000, LocalDate.parse("2021-02-28")));
        listStack.add(new Stack(1, 3, 20, 5000, LocalDate.parse("2021-02-28")));

        listStack.add(new Stack(2, 1, 40, 5000, LocalDate.parse("2021-03-05")));
        listStack.add(new Stack(2, 2, 50, 7000, LocalDate.parse("2021-03-05")));
        listStack.add(new Stack(2, 3, 20, 3000, LocalDate.parse("2021-03-05")));

        List<Stock> listStock = new ArrayList<>();
        listStock.add(new Stock(200, 11700, LocalDate.parse("2021-02-12")));
        listStock.add(new Stock(15, 8000, LocalDate.parse("2021-02-15")));
        listStock.add(new Stock(30, 7995, LocalDate.parse("2021-02-15")));

        final List<Integer> orders = new ArrayList<>();
        final List<LocalDate> ordersDate = new ArrayList<>();
        listStack.forEach(item -> {
            orders.addAll(Collections.nCopies(item.getQuantity(), item.getLength()));
            ordersDate.addAll(Collections.nCopies(item.getQuantity(), item.getDeliveryDate()));
        });

        final List<Integer> stocks = new ArrayList<>();
        final List<LocalDate> stocksDate = new ArrayList<>();
        listStock.forEach(item -> {
            stocks.addAll(Collections.nCopies(item.getQuantity(), item.getLength()));
            stocksDate.addAll(Collections.nCopies(item.getQuantity(), item.getImportDate()));
        });

        final List<Machine> machines = new ArrayList<>();
        machines.add(new Machine(0, 1, LocalDate.parse("2021-02-20"), 240));
        machines.add(new Machine(0, 1, LocalDate.parse("2021-02-20"), 240));
        machines.add(new Machine(0, 1, LocalDate.parse("2021-02-20"), 240));

        Triplet<List<Integer>, List<Integer>, List<String>> triplet = getMessageFromGreedyAlgorithm(orders, stocks, ordersDate, stocksDate, machines);

        if (!triplet.getValue0().isEmpty()) {
            outputStatistic(triplet.getValue0(), triplet.getValue1(), triplet.getValue2(), machines, listStack, orders, stocks);
        } else {
            System.out.println("can't resolve");
        }
    }

    public static void testCase3() {
        /*"5623,1009,1640,1640,13000,13000,13000,13000,13000,13000,|1250,1250,1250,1200,1200,1000,1000,1000,1000|5";*/
        List<Stack> listStack = new ArrayList<>();
        listStack.add(new Stack(1, 1, 3, 1250, LocalDate.parse("2021-02-28")));
        listStack.add(new Stack(1, 2, 2, 1200, LocalDate.parse("2021-02-28")));
        listStack.add(new Stack(1, 3, 4, 1000, LocalDate.parse("2021-02-28")));

        List<Stock> listStock = new ArrayList<>();
        listStock.add(new Stock(6, 11700, LocalDate.parse("2021-02-15")));
        listStock.add(new Stock(1, 5623, LocalDate.parse("2021-02-12")));
        listStock.add(new Stock(1, 1009, LocalDate.parse("2021-02-12")));
        listStock.add(new Stock(2, 1640, LocalDate.parse("2021-02-12")));


        final List<Integer> orders = new ArrayList<>();
        final List<LocalDate> ordersDate = new ArrayList<>();
        listStack.forEach(item -> {
            orders.addAll(Collections.nCopies(item.getQuantity(), item.getLength()));
            ordersDate.addAll(Collections.nCopies(item.getQuantity(), item.getDeliveryDate()));
        });

        final List<Integer> stocks = new ArrayList<>();
        final List<LocalDate> stocksDate = new ArrayList<>();
        listStock.forEach(item -> {
            stocks.addAll(Collections.nCopies(item.getQuantity(), item.getLength()));
            stocksDate.addAll(Collections.nCopies(item.getQuantity(), item.getImportDate()));
        });

        final List<Machine> machines = new ArrayList<>();
        machines.add(new Machine(0, 1, LocalDate.parse("2021-02-20"), 240));
        machines.add(new Machine(0, 1, LocalDate.parse("2021-02-20"), 240));
        machines.add(new Machine(0, 1, LocalDate.parse("2021-02-20"), 240));

        Triplet<List<Integer>, List<Integer>, List<String>> triplet = getMessageFromGreedyAlgorithm(orders, stocks, ordersDate, stocksDate, machines);

        if (!triplet.getValue0().isEmpty()) {
            outputStatistic(triplet.getValue0(), triplet.getValue1(), triplet.getValue2(), machines, listStack, orders, stocks);
        } else {
            System.out.println("can't resolve");
        }
    }

    public static void testCase2() {
        /*"11700,11700,11700,11700,11700,11700,11700,11700,11700,11700|2000,2000,2000,2000,2000,3000,3000,3000,3000,3000,3000,3000,3000,3000,3000|0"*/
        List<Stack> listStack = new ArrayList<>();
        listStack.add(new Stack(1, 1, 20, 2000, LocalDate.parse("2021-02-28")));
        listStack.add(new Stack(1, 2, 10, 3000, LocalDate.parse("2021-02-28")));

        List<Stock> listStock = new ArrayList<>();
        listStock.add(new Stock(10, 11700, LocalDate.parse("2021-02-15")));

        final List<Integer> orders = new ArrayList<>();
        final List<LocalDate> ordersDate = new ArrayList<>();
        listStack.forEach(item -> {
            orders.addAll(Collections.nCopies(item.getQuantity(), item.getLength()));
            ordersDate.addAll(Collections.nCopies(item.getQuantity(), item.getDeliveryDate()));
        });

        final List<Integer> stocks = new ArrayList<>();
        final List<LocalDate> stocksDate = new ArrayList<>();
        listStock.forEach(item -> {
            stocks.addAll(Collections.nCopies(item.getQuantity(), item.getLength()));
            stocksDate.addAll(Collections.nCopies(item.getQuantity(), item.getImportDate()));
        });

        final List<Machine> machines = new ArrayList<>();
        machines.add(new Machine(0, 1, LocalDate.parse("2021-02-20"), 240));
        machines.add(new Machine(0, 1, LocalDate.parse("2021-02-20"), 240));
        machines.add(new Machine(0, 1, LocalDate.parse("2021-02-20"), 240));

        Triplet<List<Integer>, List<Integer>, List<String>> triplet = getMessageFromGreedyAlgorithm(orders, stocks, ordersDate, stocksDate, machines);

        if (!triplet.getValue0().isEmpty()) {
            outputStatistic(triplet.getValue0(), triplet.getValue1(), triplet.getValue2(), machines, listStack, orders, stocks);
        } else {
            System.out.println("can't resolve");
        }
    }

    public static void testCase4() {
        List<Stack> listStack = new ArrayList<>();
        listStack.add(new Stack(1, 1, 2000, 2000, LocalDate.parse("2021-12-28")));
        listStack.add(new Stack(1, 2, 1000, 3000, LocalDate.parse("2021-12-28")));
        listStack.add(new Stack(1, 2, 3000, 5000, LocalDate.parse("2021-12-28")));
        listStack.add(new Stack(1, 2, 4000, 7000, LocalDate.parse("2021-12-28")));

        List<Stock> listStock = new ArrayList<>();
        listStock.add(new Stock(10000, 11700, LocalDate.parse("2021-02-15")));

        final List<Integer> orders = new ArrayList<>();
        final List<LocalDate> ordersDate = new ArrayList<>();
        listStack.forEach(item -> {
            orders.addAll(Collections.nCopies(item.getQuantity(), item.getLength()));
            ordersDate.addAll(Collections.nCopies(item.getQuantity(), item.getDeliveryDate()));
        });

        final List<Integer> stocks = new ArrayList<>();
        final List<LocalDate> stocksDate = new ArrayList<>();
        listStock.forEach(item -> {
            stocks.addAll(Collections.nCopies(item.getQuantity(), item.getLength()));
            stocksDate.addAll(Collections.nCopies(item.getQuantity(), item.getImportDate()));
        });

        final List<Machine> machines = new ArrayList<>();
        machines.add(new Machine(0, 1, LocalDate.parse("2021-02-20"), 240));
        machines.add(new Machine(0, 1, LocalDate.parse("2021-02-20"), 240));
        machines.add(new Machine(0, 1, LocalDate.parse("2021-02-20"), 240));

        Triplet<List<Integer>, List<Integer>, List<String>> triplet = getMessageFromGreedyAlgorithm(orders, stocks, ordersDate, stocksDate, machines);

        if (!triplet.getValue0().isEmpty()) {
            outputStatistic(triplet.getValue0(), triplet.getValue1(), triplet.getValue2(), machines, listStack, orders, stocks);
        } else {
            System.out.println("can't resolve");
        }
    }

    private static void outputStatistic(List<Integer> chromosome, List<Integer> chromosomeMachine, List<String> chromosomeTime, List<Machine> machines, List<Stack> stacks, List<Integer> orders, List<Integer> stocks) {
        AtomicInteger indexBeginStack = new AtomicInteger();
        System.out.println("Best solution stocks index: " + chromosome.stream().map(String::valueOf).collect(Collectors.joining(",")));
        System.out.println("Best solution machines index: " + chromosomeMachine.stream().map(String::valueOf).collect(Collectors.joining(",")));
        System.out.println("Best solution datetime cut product: " + chromosomeTime.stream().map(String::valueOf).collect(Collectors.joining(",")));
        System.out.println("Best value remain: " + getWeightOfARN(chromosome, chromosomeMachine, stocks, orders, machines));
        System.out.println("Best number stocks: " + chromosome.stream().distinct().count());
        System.out.println("Best rate remain: " + getRateRemain(chromosome, chromosomeMachine, stocks, orders, machines));
        System.out.println();

        stacks.forEach(stack -> {
            System.out.println("Item: " + stack.getIndexStack() + " -- Consignment: " + stack.getIndexConsign() + " -- Length: " + stack.getLength());
            List<Integer> subListIndexMachine = chromosomeMachine.subList(indexBeginStack.get(), indexBeginStack.get() + stack.getQuantity());
            List<String> subListIndexTime = chromosomeTime.subList(indexBeginStack.get(), indexBeginStack.get() + stack.getQuantity());
            for (int indexMachine = 0; indexMachine < machines.size(); indexMachine++) {
                int finalIndexMachine = indexMachine;
                long mount = subListIndexMachine.stream().filter(item -> item.equals(finalIndexMachine)).count();
                if (mount > 0) {
                    System.out.println("Machine " + (indexMachine + 1) + " cuts " + mount + "/" + stack.getQuantity()
                            + " of this, starting from the moment " + subListIndexTime.get(subListIndexMachine.indexOf(finalIndexMachine)));
                }

            }
            System.out.println("====================================================================");
            indexBeginStack.addAndGet(stack.getQuantity());
        });
        System.out.println();
    }

    /**
     * chon ra phuong phap co phan thua tot nhat trong list danh sach co cung so luong thanh nguyen lieu can dung
     *
     * @param orders danh sach san pham yeu cau
     * @param stocks danh sach nguyen lieu co the dung
     * @return String
     */
    public static Triplet<List<Integer>, List<Integer>, List<String>> getMessageFromGreedyAlgorithm(List<Integer> orders, List<Integer> stocks, List<LocalDate> ordersDate, List<LocalDate> stocksDate, List<Machine> machines) {
        String order = orders.stream().map(String::valueOf).collect(Collectors.joining(COMMA));
        String stock = stocks.stream().map(String::valueOf).collect(Collectors.joining(COMMA));

        if (stock.trim().equals(BLANK)) return Triplet.with(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        List<FastCutBean> listArn = new ArrayList<>();
        int[] messageFromArnAlgorithm;
        /*
         * arrOrderInit = 2000, 3000, 5000, 1500, 7000
         */
        int[] arrOrderInit = Arrays.stream(order.split(COMMA))
                .map(s -> Integer.parseInt(s.trim()))
                .mapToInt(Integer::intValue)
                .toArray();

        /*
         * sắp xếp mảng order theo thứ tự từ cao đến thấp
         * arrOrder = 7000, 5000, 3000, 2000, 1500
         */
        int[] arrOrder = IntStream.of(arrOrderInit)
                .boxed()
                .sorted(Comparator.reverseOrder())
                .mapToInt(Integer::intValue)
                .toArray();

        /*
         * sắp xếp lại chỉ số index trong mảng order ban đầu theo thứ tự từ cao đến thấp (của giá trị)
         * sortedIndicesOrder = 4, 2, 1, 0, 3
         */
        int[] sortedIndicesOrder = IntStream.range(0, arrOrderInit.length)
                .boxed()
                .sorted(Comparator.comparing(i -> -arrOrderInit[i]))
                .mapToInt(Integer::intValue)
                .toArray();

        int[] arrStockInit = Arrays.stream(stock.split(COMMA))
                .map(s -> Integer.parseInt(s.trim()))
                .mapToInt(Integer::intValue)
                .toArray();

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

        fastCutMain(listArn, arrCheckMaterialCanBeCut, numberMaterialRemoved, arrStock, arrOrder);

        listArn = listArn.stream()
                .sorted(Comparator.comparing(FastCutBean::getNumberMaterial)
                        .thenComparing(FastCutBean::getRateRemain))
                .collect(Collectors.toList());

        if (!listArn.isEmpty()) {
            System.out.println("population size: " + listArn.size());
            for (int arnIndex = 0; arnIndex < listArn.size(); arnIndex++) {
                messageFromArnAlgorithm = listArn.get(arnIndex).getArrIndexStockUsed();
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
                List<Integer> arnStocks = Arrays.stream(arrMessageConvertForStock).boxed().collect(Collectors.toList());
                Triplet<Boolean, List<Integer>, List<String>> triplet = computeDetails(arnStocks, machines, stocks, orders, ordersDate, stocksDate);
                if (Boolean.TRUE.equals(triplet.getValue0())) {
//                    return Arrays.stream(arrMessageConvertForStock).mapToObj(String::valueOf).collect(Collectors.joining(COMMA));
                    return Triplet.with(arnStocks, triplet.getValue1(), triplet.getValue2());
                }
            }
        }
        return Triplet.with(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public static String getRateRemain(List<Integer> currentARNStocks, List<Integer> currentARNMachines, List<Integer> stocks, List<Integer> orders, List<Machine> machines) {
        List<Integer> uniqueStocks = currentARNStocks.stream().distinct().sorted().collect(Collectors.toList());
        List<Integer> stockTemp = computeStockRemain(currentARNStocks, currentARNMachines, stocks, orders, machines);

        int valueRemain = 0;
        for (Integer uniqueStock : uniqueStocks) {
            valueRemain += stockTemp.get(uniqueStock);
        }

        int valueInit = 0;
        for (Integer uniqueStock : uniqueStocks) {
            valueInit += stocks.get(uniqueStock);
        }

        double scale = Math.pow(10, 2);
        return Math.round(((double) valueRemain / valueInit) * scale) + "%";
    }

    public static int getWeightOfARN(List<Integer> currentARNStocks, List<Integer> currentARNMachines, List<Integer> stocks, List<Integer> orders, List<Machine> machines) {
        List<Integer> uniqueStocks = currentARNStocks.stream().distinct().sorted().collect(Collectors.toList());
        List<Integer> stockTemp = computeStockRemain(currentARNStocks, currentARNMachines, stocks, orders, machines);

        int value = 0;
        for (Integer uniqueStock : uniqueStocks) {
            value += stockTemp.get(uniqueStock);
        }

        return value;
    }

    public static List<Integer> computeStockRemain(List<Integer> currentARNStocks, List<Integer> currentARNMachines, List<Integer> stocks, List<Integer> orders, List<Machine> machines) {
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


    public static int getMostFreeIndexForCurrentState(List<Machine> machinesState, int stockIndex, int orderIndex, List<Integer> currentARNStocks, List<Integer> currentARNMachines, List<LocalDate> ordersDate, List<LocalDate> stocksDate) {
        Optional<Machine> machines = machinesState.stream()
                .sorted(Comparator
                        .comparing(Machine::getFreeDate)
                        .thenComparingInt(Machine::getMinutesRemaining).reversed()
                )
                .filter(o -> o.getFreeDate().compareTo(ordersDate.get(orderIndex)) < 0 && stocksDate.get(stockIndex).compareTo(ordersDate.get(orderIndex)) < 0)
                .findFirst();
        int indexStockOfARN = currentARNStocks.indexOf(stockIndex);
        if (indexStockOfARN == -1) {
            return machines.map(machinesState::indexOf).orElse(-1);
        } else {
            return currentARNMachines.get(indexStockOfARN);
        }
    }

    public static Triplet<Boolean, List<Integer>, List<String>> computeDetails(List<Integer> arnStocks, List<Machine> machines, List<Integer> stocks, List<Integer> orders, List<LocalDate> ordersDate, List<LocalDate> stocksDate) {
        boolean aBoolean = true;
        List<Integer> stockState = new ArrayList<>(stocks);
        List<Integer> currentARNMachines = new ArrayList<>();
        List<String> currentARNTimes = new ArrayList<>();
        List<Integer> currentARNStocks = new ArrayList<>();
        List<Machine> machinesState = new ArrayList<>();
        for (Machine machine : machines) {
            machinesState.add(machine.clone());
        }
        for (int orderIndex = 0; orderIndex < arnStocks.size(); orderIndex++) {
            int stockIndex = arnStocks.get(orderIndex);
            int indexMostFree = getMostFreeIndexForCurrentState(machinesState, stockIndex, orderIndex, currentARNStocks, currentARNMachines, ordersDate, stocksDate);
            if (indexMostFree == -1) {
                aBoolean = false;
            }
            if (orders.get(orderIndex).equals(stockState.get(stockIndex))) {
                // update stocks
                stockState.set(stockIndex, stockState.get(stockIndex) - orders.get(orderIndex));
                currentARNStocks.add(stockIndex);
                currentARNMachines.add(indexMostFree);
                currentARNTimes.add(machinesState.get(indexMostFree).getRealTime());
            } else {
                int cutWidth = machinesState.get(indexMostFree).getBladeThickness();
                if (orders.get(orderIndex) + cutWidth <= stockState.get(stockIndex)) {
                    // update stocks state
                    stockState.set(stockIndex, stockState.get(stockIndex) - orders.get(orderIndex) - cutWidth);
                    currentARNStocks.add(stockIndex);
                    currentARNMachines.add(indexMostFree);
                    currentARNTimes.add(machinesState.get(indexMostFree).getRealTime());

                    // update machines state
                    Machine currentMachine = machinesState.get(indexMostFree);
                    int minutesRemain = currentMachine.getMinutesRemaining() - currentMachine.getFrequency();
                    if (minutesRemain > Machine.MIN_MINUTES_REMAINING) {
                        currentMachine.setMinutesRemaining(minutesRemain);
                    } else {
                        currentMachine.setMinutesRemaining(Machine.MAX_MINUTES_REMAINING);
                        currentMachine.plusDay();
                    }
                }
            }
        }

        return new Triplet<>(aBoolean, currentARNMachines, currentARNTimes);
    }

    /**
     * chon ra phuong phap co phan thua tot nhat trong list danh sach co cung so luong thanh nguyen lieu can dung
     *
     * @param orders danh sach san pham yeu cau
     * @param stocks danh sach nguyen lieu co the dung
     * @return String
     */
    public static String getMessageFromFastCut(String orders, String stocks) {
        if (stocks.trim().equals(BLANK)) return BLANK;

        List<FastCutBean> listArn = new ArrayList<>();
        int[] messageFromArnAlgorithm;
        /*
         * arrOrderInit = 2000, 3000, 5000, 1500, 7000
         */
        int[] arrOrderInit = Arrays.stream(orders.split(COMMA))
                .map(s -> Integer.parseInt(s.trim()))
                .mapToInt(Integer::intValue)
                .toArray();

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

        int[] arrStockInit = Arrays.stream(stocks.split(COMMA))
                .map(s -> Integer.parseInt(s.trim()))
                .mapToInt(Integer::intValue)
                .toArray();

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

        fastCutMain(listArn, arrCheckMaterialCanBeCut, numberMaterialRemoved, arrStock, arrOrder);

        if (!listArn.isEmpty()) {
            listArn = listArn.stream()
                    .sorted(Comparator.comparing(FastCutBean::getNumberMaterial)
                            .thenComparing(FastCutBean::getRateRemain))
                    .collect(Collectors.toList());

            messageFromArnAlgorithm = listArn.get(0).getArrIndexStockUsed();
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
            String arn = Arrays.stream(arrMessageConvertForStock).mapToObj(String::valueOf).collect(Collectors.joining(COMMA));
            System.out.println("Best solution: " + arn);
            System.out.println("Best value rate: " + listArn.get(0).getRateRemain());
            System.out.println("Best value remain: " + listArn.get(0).getRemain());
            System.out.println("Best value stockUsed: " + listArn.get(0).getStockUsed());
            return arn;
        } else {
            return BLANK;
        }
    }

    /**
     * Thực hiện cắt bắt đầu từ các thanh có độ dài lớn nhất, sau đó cứ giảm bớt đi các thanh có độ dài từ cao đến thấp (numberMaterialRemoved++)
     * cho đến khi không thể cắt được nữa -> dừng lại.
     * Trong đó có các thanh được đánh dấu là môt bộ(phải được cắt từ 1 thanh nguyên liệu)
     *
     * @param listArn                  luu danh sach cac ket qua tot nhat
     * @param arrCheckMaterialCanBeCut dung de loai bo dan cac thanh co do dai lon
     * @param numberMaterialRemoved    so luong cac thanh sat da bi loai bo so voi ban dau
     * @param arrStock                 danh sach nguyen lieu trong kho
     * @param arrOrder                 danh sach san pham yeu cau
     */
    private static void fastCutMain(List<FastCutBean> listArn, int[] arrCheckMaterialCanBeCut,
                                    int numberMaterialRemoved, int[] arrStock, int[] arrOrder) {
        int unit = Integer.parseInt(STEEL_BLADE_THICKNESS);
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
                            remaining -= (arrOrder[j] + unit);
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
            int numberMaterial = (int) Arrays.stream(arrIndexStockUsed).distinct().count();

            Supplier<IntStream> otherRemainStock = () -> IntStream.range(0, arrRemain.length)
                    .filter(i -> arrRemain[i] != arrStock[i] && Arrays.stream(arrIndexStockUsed).anyMatch(index -> index == i));
            /*
             * tổng phần thừa với cách cắt tương ứng
             */
            int remain = otherRemainStock.get().mapToObj(i -> arrRemain[i]).mapToInt(Integer::intValue).sum();
            int stockUsed = otherRemainStock.get().mapToObj(i -> arrStock[i]).mapToInt(Integer::intValue).sum();

            double rateRemain = (double) remain / stockUsed;

            listArn.add(new FastCutBean(numberMaterial, rateRemain, arrIndexStockUsed, remain, stockUsed));
            arrCheckMaterialCanBeCut[numberMaterialRemoved++] = 0;
            fastCutMain(listArn, arrCheckMaterialCanBeCut, numberMaterialRemoved, arrStock, arrOrder);
        }
    }
}
