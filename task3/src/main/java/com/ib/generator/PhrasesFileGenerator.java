package com.ib.generator;

import com.ib.util.Strings;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * Created by lukasz.odulinski on 28.10.2016.
 */
public class PhrasesFileGenerator {

    public static final int PHRASES_PER_LINE_COUNT = 50;

    private String dictionaryFilename;
    private String outputFilename;
    private long seed;
    private long phrasesLinesCount;

    public PhrasesFileGenerator(GeneratorParameters parameters){
        dictionaryFilename = parameters.getDictionaryFilename();
        outputFilename = parameters.getOutputFilename();
        seed = parameters.getSeed();
        phrasesLinesCount = parameters.getPhrasesLinesCount();

    }

    public void generate() throws IOException {
        List<String> words = distinctWords(dictionaryFilename);
        Random random = initRandom();

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFilename))) {
            Stream.generate(() -> generatePhrases(words, PHRASES_PER_LINE_COUNT, random))
                    .limit(phrasesLinesCount)
                    .forEach(line -> write(writer, line));
        }

    }

    private  List<String> distinctWords(String filename) throws IOException {
        return Files.lines(Paths.get(filename))
                .flatMap(PhrasesFileGenerator::toWords)
                .filter(word -> word.length() > 4)
                .distinct().collect(toList());
    }

    private static Stream<String> toWords(String line) {
        return Strings.toWords(line.replaceAll("[^a-zA-Z ]", "").toLowerCase(), " ");
    }


    private static void write(BufferedWriter writer, String line) {
        try {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String generatePhrases(List<String> words, int phraseCount, Random random) {
        return Stream.generate(() -> generatePhrase(words, random))
                .limit(phraseCount)
                .collect(joining(" | "));
    }

    private static String generatePhrase(List<String> words, Random random) {
        int numberOfWordsInPhrase = random.nextInt(2) + 1;
        return Stream.generate(() -> pickRandomWord(words, random))
                .limit(numberOfWordsInPhrase)
                .collect(joining(" "));
    }

    private static String pickRandomWord(List<String> words, Random random) {
        return words.get(random.nextInt(words.size()));
    }

    private  Random initRandom() {
        return new Random(seed);
    }


    public static class GeneratorParameters {
        private String dictionaryFilename;
        private String outputFilename;
        private long seed;
        private long phrasesLinesCount;

        public String getDictionaryFilename() {
            return dictionaryFilename;
        }

        public String getOutputFilename() {
            return outputFilename;
        }

        public long getSeed() {
            return seed;
        }

        public long getPhrasesLinesCount() {
            return phrasesLinesCount;
        }

        public GeneratorParameters withDictionaryFilename(String dictionaryFilename) {
            this.dictionaryFilename = dictionaryFilename;
            return this;
        }

        public GeneratorParameters withOutputFilename(String outputFilename) {
            this.outputFilename = outputFilename;
            return this;
        }

        public GeneratorParameters withSeed(long seed) {
            this.seed = seed;
            return this;
        }

        public GeneratorParameters withPhrasesLinesCount(long phrasesLinesCount) {
            this.phrasesLinesCount = phrasesLinesCount;
            return this;
        }



    }
}
