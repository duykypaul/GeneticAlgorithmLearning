package com.duykypaul;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class Test {
    public static void main(String[] args) {
        List<Integer> ints = Arrays.asList(7,5,3,8,9,0,0,0,0,0,0,9,8,7,6,5,4,3,2,1);
        int k = 4;
        System.out.println("findKthLargest: " + findKthLargest(ints, k));
        System.out.println("findKthSmallest: " + findKthSmallest(ints, k));
    }

    private static Integer findKthLargest(List<Integer> ints, int k) {
        ints = ints.stream().distinct().collect(Collectors.toList());
        if(ints.size() < k) return -1;
        PriorityQueue<Integer> priorityQueue = new PriorityQueue<>(ints.subList(0, k));
        for (int i = k; i < ints.size(); i++) {
            if(!priorityQueue.isEmpty() && ints.get(i) > priorityQueue.peek()) {
                priorityQueue.poll();
                priorityQueue.add(ints.get(i));
            }
        }
        return priorityQueue.peek();
    }

    private static Integer findKthSmallest(List<Integer> ints, int k) {
        ints = ints.stream().distinct().collect(Collectors.toList());
        if(ints.size() < k) return -1;
        PriorityQueue<Integer> priorityQueue = new PriorityQueue<>(Collections.reverseOrder());
        priorityQueue.addAll(ints.subList(0, k));
        for (int i = k; i < ints.size(); i++) {
            if(!priorityQueue.isEmpty() && ints.get(i) < priorityQueue.peek()) {
                priorityQueue.poll();
                priorityQueue.add(ints.get(i));
            }
        }
        return priorityQueue.peek();
    }
}
