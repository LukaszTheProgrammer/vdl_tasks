package com.ib.finder;

import com.ib.util.BulkReader;
import com.ib.util.Strings;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;
import static org.apache.commons.collections4.ListUtils.partition;

/**
 * Created by lukasz.odulinski on 29.10.2016.
 */
public class PopularPhrasesFinder {

    private static final int BATCH_SIZE = 10000;
    private static final int NUMBER_OF_THREADS = 2;
    private static final int PARTITION_SIZE = BATCH_SIZE / NUMBER_OF_THREADS;

    private final BulkReader sourceOfPhrases;

    private PopularPhrasesFinder(BulkReader sourceOfPhrases) {
        this.sourceOfPhrases = sourceOfPhrases;
    }

    public static PopularPhrasesFinder of(BulkReader bulkReader) {
        return new PopularPhrasesFinder(bulkReader);
    }

    public List<Map.Entry<String, Long>> findNMostPopularPhrases(long phrasesCount) {
        return sort(createIndex()).limit(phrasesCount).collect(toList());
    }

    private Map<String, Long> createIndex() {
        Map<String, Long> occurencesByPhrases = new ConcurrentHashMap<>();

        while (sourceOfPhrases.hasMore()) {
            computePartialIndexConcurrently(sourceOfPhrases.readLines(BATCH_SIZE))
                    .forEach(phraseWithOccurrence -> merge(phraseWithOccurrence, occurencesByPhrases));
        }
        return occurencesByPhrases;
    }

    private Stream<Map.Entry<String, Long>> computePartialIndexConcurrently(List<String> linesOfPhrases) {
        return partition(linesOfPhrases, PARTITION_SIZE)
                .parallelStream()
                .flatMap(this::createOccurencesByPhrasesIndex);
    }

    private Stream<Map.Entry<String, Long>> createOccurencesByPhrasesIndex(List<String> linesOfPhrases) {
        return index(extractPhrases(linesOfPhrases)).entrySet().stream();
    }

    private Stream<String> extractPhrases(List<String> lines) {
        return lines.stream().flatMap(line -> Strings.toWords(line, "[|]")).map(String::trim);
    }

    private Map<String, Long> index(Stream<String> phrasesStream) {
        return phrasesStream.collect(groupingBy(identity(), counting()));
    }

    private void merge(Map.Entry<String, Long> entry, Map<String, Long> occurencesByPhrase) {
        occurencesByPhrase.merge(entry.getKey(), entry.getValue(), (oldValue, newValue) -> oldValue + newValue);
    }

    private Stream<Map.Entry<String, Long>> sort(Map<String, Long> occurrencesByPhrases) {
        return occurrencesByPhrases.entrySet().stream().sorted(byOccurenceDescending());

    }

    private Comparator<Map.Entry<String, Long>> byOccurenceDescending() {
        return (e1, e2) -> e2.getValue().compareTo(e1.getValue());
    }
}
