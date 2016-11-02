package com.ib.list.algorithm;

import org.junit.Test;

import java.util.Collections;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

/**
 * Created by lukasz.odulinski on 31.10.2016.
 */
public class ComplementaryPairFinderTest {
    @Test
    public void shouldFindZeroKComplementaryPairsInEmptyList() {
        //WHEN
        long result = Algorithms.countKcomplementaryPairs(Collections.<Integer>emptyList(), 10);
        //THEN
        assertEquals(0, result);
    }

    @Test
    public void shouldFindZeroKComplementaryPairsIfThereIsNone() {
        //WHEN
        long result = Algorithms.countKcomplementaryPairs(asList(1, 2, 3, 6), 10);
        //THEN
        assertEquals(0, result);
    }

    @Test
    public void shouldFindTwoKComplementaryPairs() {
        //WHEN
        long result = Algorithms.countKcomplementaryPairs(asList(1, 2, 3, 7), 10);
        //THEN
        assertEquals(2, result);
    }

    @Test
    public void shouldFindZeroKComplementaryPairsInASetOfGreaterThanKNumbers() {
        //WHEN
        long result = Algorithms.countKcomplementaryPairs(asList( 11, 12, 13, 14), 10);
        //THEN
        assertEquals(0, result);
    }

    @Test
    public void aNumberThatIsAHalfOfKShouldBeConsideredAPairOfItself() {
        //WHEN
        long result = Algorithms.countKcomplementaryPairs(asList(1, 5, 1, 5), 10);
        //THEN
        assertEquals(4, result);
    }

    @Test
    public void shouldCountMultipleOccurencesOfTheSamePair() {
        //WHEN
        long result = Algorithms.countKcomplementaryPairs(asList(1, 3, 4, 2, 7, 3, 1, 3, 2, 7), 10);
        //THEN
        assertEquals(12, result);
    }

    @Test
    public void shouldIndicateThatKIsComplementaryWithZero() {
        //WHEN
        long result = Algorithms.countKcomplementaryPairs(asList(0,1,10), 10);
        //THEN
        assertEquals(2, result);
    }

}
