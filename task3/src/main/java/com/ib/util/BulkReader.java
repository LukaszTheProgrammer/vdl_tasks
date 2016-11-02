package com.ib.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukasz.odulinski on 30.10.2016.
 */
public class BulkReader {
    private final BufferedReader reader;
    private boolean hasMore = true;

    public BulkReader(BufferedReader reader) {
        this.reader = reader;
    }

    public List<String> readLines(int linesCount){
        String line;
        List<String> lines = new ArrayList<>();
        while((line = readLine()) != null){
            lines.add(line);
            if(lines.size() >= linesCount){
                return lines;
            }
        }

        hasMore = false;
        return lines;

    }

    public boolean hasMore(){
        return hasMore;
    }

    private String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
