package com.ib.duplicate;

import com.ib.generator.PhoneBookGenerator;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created by lukasz.odulinski on 30.10.2016.
 */
public class DuplicateNumberFinderTest {

    public static final String INPUT = System.getProperty("user.dir") + "/src/main/resources/phone_numbers.txt";

    @Before
    public void setup() {
        if (!(new File(INPUT).exists())) {
            PhoneBookGenerator.generate(INPUT, 100_000_000, 6745132);
        }
    }

    @Test
    public void shouldFindDuplicateNumber() throws IOException {
        DuplicateNumberFinder duplicateNumberFinder = new DuplicateNumberFinder();
        assertEquals("0019503830", duplicateNumberFinder.find(INPUT).get());
    }
}
