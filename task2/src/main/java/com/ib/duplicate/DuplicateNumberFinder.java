package com.ib.duplicate;

import com.ib.reader.BlockReader;
import com.ib.util.Strings;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.BitSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.lang.System.getProperty;
import static java.nio.file.Files.newBufferedReader;
import static java.nio.file.Paths.get;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;

/**
 * Created by lukasz.odulinski on 30.10.2016.
 */
public class DuplicateNumberFinder {
    private static final String SEP = File.separator;
    private static final String CHUNK_FILES_FOLDER = getProperty("user.dir") + SEP + "tmp" + SEP;
    private static final int CHUNK_SIZE = 50_000_000;
    private static final int CHUNKS_COUNT = 200;

    public Optional<String> find(String filename) throws IOException {
        prepareChunkFilesFolder();
        divideNumbersToChunks(filename);
        return searchDuplicatesInChunks();
    }

    private void prepareChunkFilesFolder() throws IOException {
        File chunkFilesFolder = new File(CHUNK_FILES_FOLDER);
        if(chunkFilesFolder.exists())
            FileUtils.cleanDirectory(chunkFilesFolder);
        else
            FileUtils.forceMkdir(chunkFilesFolder);
    }

    private void divideNumbersToChunks(String filename) throws IOException {
        logMemoryUsage();

        try (BufferedReader reader = newBufferedReader(get(filename))) {
            BlockReader blockReader = new BlockReader(reader);
            List<BufferedWriter> chunkWriters = getChunkWriters();
            while (blockReader.hasMore()) {
                storeInFileChunks(extractNumbers(blockReader.nextBlock()), chunkWriters);
            }
            chunkWriters.stream().forEach(this::close);
            logMemoryUsage();
        }

    }

    private List<BufferedWriter> getChunkWriters() {
        return IntStream.range(0, CHUNKS_COUNT).boxed().map(this::getChunkWriter).collect(toList());
    }

    private BufferedWriter getChunkWriter(Integer i) {
        try {
            return Files.newBufferedWriter(get(CHUNK_FILES_FOLDER + chunkFilename(i)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String chunkFilename(Integer i) {
        return String.format("tmp_%d.txt", i);
    }

    private List<Long> extractNumbers(String block) {
        return Strings.toWords(block, "\\s")
                .filter(StringUtils::isNumeric)
                .filter(n -> n.length() == 10)
                .map(Long::valueOf)
                .collect(toList());
    }

    private void storeInFileChunks(List<Long> phoneNumbers, List<BufferedWriter> writers) {
        phoneNumbers.stream().forEach(phoneNumber -> storeInFileChunk(phoneNumber, writers));
    }

    private void storeInFileChunk(Long phoneNumber, List<BufferedWriter> writers) {
        try {
            writers.get(calculateFileChunkIndex(phoneNumber)).write(format(phoneNumber) + getProperty("line.separator"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int calculateFileChunkIndex(Long phoneNumber) {
        return (int) (phoneNumber / CHUNK_SIZE);
    }

    private String format(Long phoneNumber) {
        return String.format("%010d", phoneNumber);
    }

    private void close(BufferedWriter writer) {
        try {
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<String> searchDuplicatesInChunks() throws IOException {

        for (int i = 0; i < CHUNKS_COUNT; ++i) {
            try (BufferedReader reader = newBufferedReader(get(CHUNK_FILES_FOLDER + chunkFilename(i)))) {
                String line;
                BitSet existingPhoneNumbers = new BitSet(CHUNK_SIZE);
                while ((line = reader.readLine()) != null) {
                    int phoneNumberIndex = calculatePhoneNumberIndex(i, line);
                    if (existingPhoneNumbers.get(phoneNumberIndex)) {
                        return Optional.of(line);
                    } else {
                        existingPhoneNumbers.flip(phoneNumberIndex);
                    }
                }
                logMemoryUsage();
            }
        }
        return empty();
    }

    private int calculatePhoneNumberIndex(int i, String line) {
        return (int) (Long.parseLong(line) - CHUNK_SIZE * i);
    }


    private void logMemoryUsage() {
        int mb = 1024 * 1024;

        Runtime runtime = Runtime.getRuntime();

        System.out.println("##### Heap utilization statistics [MB] #####");

        System.out.println("Used Memory:"
                + (runtime.totalMemory() - runtime.freeMemory()) / mb + " MB");

        System.out.println("Free Memory:"
                + runtime.freeMemory() / mb + " MB");

        System.out.println("Total Memory:" + runtime.totalMemory() / mb + " MB");

        System.out.println("Max Memory:" + runtime.maxMemory() / mb + " MB");
    }
}
