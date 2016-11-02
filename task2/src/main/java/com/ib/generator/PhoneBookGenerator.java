package com.ib.generator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

/**
 * Created by lukasz.odulinski on 30.10.2016.
 */
public class PhoneBookGenerator {
    private static final List<String> firstNames = asList("Jack", "John", "Jim", "Jennifer", "Ben", "Bill", "Beth", "Anna", "Angela", "Alexis");
    private static final  List<String> lastNames = asList("Smith", "Doe", "Jackson", "Jordan", "White", "Brown", "Ho", "Water", "Johnson", "Harris");

    public static void generate(String outputFilename, int phoneNumberCount, int seed) {
        Random random = new Random(seed);
        try {
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFilename))) {

                Stream.generate(getLongSupplier(random))
                        .limit(phoneNumberCount)
                        .forEach(n -> {
                            try {
                                writer.write(generateName(random));
                                writer.write(String.format(" %010d ", n));
                                if (random.nextInt() % 5 == 0) writer.newLine();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String generateName(Random random) {
        return firstNames.get(Math.abs(random.nextInt() % firstNames.size())) + " "+ lastNames.get(Math.abs(random.nextInt() % lastNames.size()));
    }

    private static Supplier<Long> getLongSupplier(Random random) {
        return () -> Math.abs(random.nextLong() % 10_000_000_000L);
    }
}
