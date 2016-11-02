package com.ib.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;

import static java.lang.System.getProperty;

/**
 * Created by lukasz.odulinski on 30.10.2016.
 */
public class BlockReader {
    public static final int BLOCK_SIZE = 1024;
    private final BufferedReader reader;
    private boolean hasMore = true;
    private String leftOver = "";

    public BlockReader(BufferedReader reader) {
        this.reader = reader;
    }

    public String nextBlock() {

        String current = "";
        while (!Objects.equals(current = read(), "")) {
            int whitespaceIndex = lastWhitespaceIndex(current);
            if (whitespaceIndex != -1) {
                String line = leftOver + current.substring(0, whitespaceIndex);
                leftOver = current.substring(whitespaceIndex, current.length());
                return line;
            } else {
                leftOver += current;
            }
        }

        hasMore = false;
        return leftOver;

    }

    private int lastWhitespaceIndex(String text) {
        return max(text.lastIndexOf(getProperty("line.separator")), text.lastIndexOf(' '), text.lastIndexOf('\t'));
    }

    private int max(Integer ... numbers) {
        return Stream.of(numbers).max(Integer::compareTo).orElse(-1);
    }

    public boolean hasMore() {
        return hasMore;
    }

    private String read() {
        try {
            char[] buffer = new char[BLOCK_SIZE];
            int bytesRead = reader.read(buffer);
            return bytesRead > 0 ? new String(buffer, 0, bytesRead) : "";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
