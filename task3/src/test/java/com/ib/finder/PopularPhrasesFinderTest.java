package com.ib.finder;

import com.ib.generator.PhrasesFileGenerator;
import com.ib.generator.PhrasesFileGenerator.GeneratorParameters;
import com.ib.util.BulkReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map.Entry;

import static java.lang.Long.valueOf;
import static org.junit.Assert.assertEquals;

/**
 * Created by lukasz.odulinski on 29.10.2016.
 */
public class PopularPhrasesFinderTest {
    private static final String WORKING_DIR = System.getProperty("user.dir");
    private static final String GENERATED_PHRASES_FILENAME = WORKING_DIR + "/phrasesFile.txt";
    private static final String DICTIONARY_FILE_LOCATION = WORKING_DIR + "/src/test/resources/dictionary.txt";
    private long startTime;

    @Before
    public void setup() throws IOException {
        if (!(new File(GENERATED_PHRASES_FILENAME).exists())) {
            new PhrasesFileGenerator(initParameters()).generate();
        }

        startTime = System.currentTimeMillis();
    }

    private GeneratorParameters initParameters() {
        return new GeneratorParameters().withDictionaryFilename(DICTIONARY_FILE_LOCATION)
                .withOutputFilename(GENERATED_PHRASES_FILENAME)
                .withPhrasesLinesCount(1500000L)  //1500000L results in a file around 1GB big
                .withSeed(3456781293303L);  //Constant seed so the generated file is the same all the time
    }

    @Test
    public void shouldFindNMostPopularPhrases() throws IOException {
        //WHEN
        List<Entry<String, Long>> mostPopularPhrases = findNMostPopularPhrases(10000L);

        //THEN
        assertPhrase(mostPopularPhrases.get(0), "anything", valueOf(124720L));
        assertPhrase(mostPopularPhrases.get(1), "lifetime", valueOf(124593L));
        assertPhrase(mostPopularPhrases.get(mostPopularPhrases.size() - 1), "surprised awoke", valueOf(434L));
    }

    private void assertPhrase(Entry<String, Long> phraseWithOccurence, String phrase, Long occurence) {
        assertEquals(phrase, phraseWithOccurence.getKey());
        assertEquals(occurence, phraseWithOccurence.getValue());
    }

    private List<Entry<String, Long>> findNMostPopularPhrases(long phrasesCount) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(GENERATED_PHRASES_FILENAME))) {
            return PopularPhrasesFinder.of(new BulkReader(reader)).findNMostPopularPhrases(phrasesCount);
        }
    }

    @After
    public void tearDown() {
        System.out.println("It took " + (System.currentTimeMillis() - startTime) + " ms");
    }


}
