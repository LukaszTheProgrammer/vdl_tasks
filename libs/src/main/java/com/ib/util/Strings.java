package com.ib.util;

import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Created by lukasz.odulinski on 29.10.2016.
 */
public class Strings {
    public static Stream<String> toWords(String line, String delimiter) {
        return Pattern.compile(delimiter).splitAsStream(line);
    }
}
