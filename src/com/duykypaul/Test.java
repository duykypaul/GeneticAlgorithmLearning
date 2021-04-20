package com.duykypaul;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Test {
    public static void main(String[] args) {
        /*List<Integer> ints = Arrays.asList(7,5,3,8,9,0,0,0,0,0,0,9,8,7,6,5,4,3,2,1);
        System.out.println( new HashSet<>(ints));*/
        Integer[] cut = new Integer[]{150, 250, 300, 250, 250};
        Integer[] idx = new Integer[]{0, 0, 0, 1, 2};
        int findIdx = 0;
        final Boolean[] check = {false};
        int l = 5;
        IntStream stream = IntStream.range(0, idx.length);
        int sum = stream.filter(e -> idx[e] == findIdx).reduce(0, (a, b) -> {
            if(!check[0]) {
                check[0] = true;
                return a + cut[b];
            } else {
                return a + cut[b] + l;
            }
        });
        System.out.println(sum);
    }

    private static Integer findKthLargest(List<Integer> ints, int k) {
        ints = ints.stream().distinct().collect(Collectors.toList());
        if (ints.size() < k) return -1;
        PriorityQueue<Integer> priorityQueue = new PriorityQueue<>(ints.subList(0, k));
        for (int i = k; i < ints.size(); i++) {
            if (!priorityQueue.isEmpty() && ints.get(i) > priorityQueue.peek()) {
                priorityQueue.poll();
                priorityQueue.add(ints.get(i));
            }
        }
        return priorityQueue.peek();
    }

    private static Integer findKthSmallest(List<Integer> ints, int k) {
        ints = ints.stream().distinct().collect(Collectors.toList());
        if (ints.size() < k) return -1;
        PriorityQueue<Integer> priorityQueue = new PriorityQueue<>(Collections.reverseOrder());
        priorityQueue.addAll(ints.subList(0, k));
        for (int i = k; i < ints.size(); i++) {
            if (!priorityQueue.isEmpty() && ints.get(i) < priorityQueue.peek()) {
                priorityQueue.poll();
                priorityQueue.add(ints.get(i));
            }
        }
        return priorityQueue.peek();
    }
}
