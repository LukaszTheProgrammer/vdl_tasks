package com.ib.list.algorithm;

import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

/**
 * Created by lukasz.odulinski on 31.10.2016.
 */
public class Algorithms {
    public static Long countKcomplementaryPairs(List<Integer> list, Integer k) {
        Map<Integer, Long> occurencesByNumbers = getOccurencesByNumbers(list);
        return occurencesByNumbers.entrySet()
                .stream()
                .map(entry -> calculateCount(entry, occurencesByNumbers, k))
                .reduce(0L, (accumulator, element) -> accumulator + element);
    }

    private static Map<Integer, Long> getOccurencesByNumbers(List<Integer> list) {
        return list.stream().collect(groupingBy(identity(), counting()));
    }

    private static long calculateCount(Map.Entry<Integer, Long> entry, Map<Integer, Long> occurencesByNumber, Integer k) {
        return entry.getValue() * occurencesByNumber.getOrDefault(k - entry.getKey(), 0L);
    }

}
