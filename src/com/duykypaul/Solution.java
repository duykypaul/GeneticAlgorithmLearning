package com.duykypaul;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

enum Kikaku {
    a,
    b,
    c,
    d
}

class Solution {
    public static void main(String[] args) {
        List<String> strings = Arrays.asList("a", "b", "c", "d", "g");
        String a = Math.random() > 0.3 ? Kikaku.a.name() : "";
        String b = Math.random() > 0.3 ? Kikaku.b.name() : "";
        String c = Math.random() > 0.3 ? Kikaku.c.name() : "";
        String d = Math.random() > 0.3 ? Kikaku.d.name() : "";
        String other = Math.random() > 0.3 ? "o" : "";
        System.out.println("a: " + a);
        System.out.println("b: " + b);
        System.out.println("c: " + c);
        System.out.println("d: " + d);
        System.out.println("other: " + other);
        List<String> conditions = Arrays.asList(a, b, c, d);
        Predicate<String> filter = x -> {
            boolean checkMain = conditions.stream().anyMatch(item -> !item.isEmpty() && x.startsWith(item));
            if (checkMain) return true;
            if (!other.isEmpty()) {
                boolean checkOther = Arrays.stream(Kikaku.values()).noneMatch(item -> x.startsWith(item.name()));
                if(checkOther) return true;
            }
            return (conditions.stream().allMatch(String::isEmpty) && other.isEmpty() || conditions.stream().noneMatch(String::isEmpty) && !other.isEmpty());
        };

        System.out.println(Arrays.toString(strings.stream().filter(filter).toArray()));
    }
}
