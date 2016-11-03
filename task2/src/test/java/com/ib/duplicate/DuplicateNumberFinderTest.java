package com.ib.duplicate;

import com.ib.generator.PhoneBookGenerator;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static java.lang.System.getProperty;
import static org.junit.Assert.assertEquals;

/**
 * Created by lukasz.odulinski on 30.10.2016.
 */
public class DuplicateNumberFinderTest {
    private static final String SEP = File.separator;
    private static final String TMP_DIRECTORY = getProperty("user.dir")+ SEP;
    private static final String INPUT = TMP_DIRECTORY + "phone_numbers.txt";

    @Before
    public void setup() {
        if (!(new File(INPUT).exists())) {
            PhoneBookGenerator.generate(INPUT, 1_000_000, 6745132);
        }
    }

    @Test
    public void shouldFindDuplicateNumber() throws IOException {
        DuplicateNumberFinder duplicateNumberFinder = new DuplicateNumberFinder();
        assertEquals("0069538185", duplicateNumberFinder.find(INPUT).orElse(""));
    }
}
