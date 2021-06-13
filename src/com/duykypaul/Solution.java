package com.duykypaul;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Solution {
    public static void main(String[] args) {
//        int[][] arr = {{1, 0}, {2, 0}, {3, 1}, {3, 2}};
//        int[][] arr = {{1, 0}};
        int[][] arr = {{2, 0}, {2, 1}};
        int k = 3;
        System.out.println(arr.length);
        System.out.println(Arrays.toString(new Solution().findOrder(k, arr)));
    }

    public int[] findOrder(int numCourses, int[][] prerequisites) {
        int[] arr = new int[numCourses];
        int count = 0;
        if (prerequisites.length == 0) {
            for (int i = 0; i < numCourses; i++) {
                arr[i] = i;
            }
            return arr;
        }
        List<Integer> lowLevel = new ArrayList<>();

        for (int[] item : prerequisites) {
            if (lowLevel.contains(item[1]) && lowLevel.contains(item[0]) && lowLevel.indexOf(item[0]) < lowLevel.indexOf(item[1])) {
                return new int[]{};
            }

            if (!lowLevel.contains(item[1])) {
                if(!lowLevel.contains(item[0])) {
                    if (count == numCourses) {
                        return lowLevel.stream().mapToInt(Integer::intValue).toArray();
                    } else {
                        count++;
                        lowLevel.add(item[1]);
                    }
                } else {
                    int indexFirstItem = lowLevel.indexOf(item[0]);
                    count++;
                    lowLevel.add(indexFirstItem, item[1]);
                }
            }
            if (count == numCourses) {
                return lowLevel.stream().mapToInt(Integer::intValue).toArray();
            } else {
                count++;
                lowLevel.add(item[0]);
            }
        }
        for (int i = count; i < numCourses; i++) {
            lowLevel.add(i);
        }
        return lowLevel.stream().mapToInt(Integer::intValue).toArray();
    }
}
