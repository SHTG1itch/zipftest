/**
 * Zipf.java
 *
 * This file is where the bulk of the your work will go.
 * The purpose is to read all the *.txt files in a folder and
 * count the frequency of the words in all those files.
 * It will return to the client code a sorted List<WordFreq> which
 * represents the frequency of words similar to Zipf's Law.
 * All frequency values must be in the range [0, 1.0].
 * The list must be sorted by frequency (largest first). In the
 * event of ties, the list will secondarily sort alphabetically.
 * 
 * The student should write many helper methods!! For example,
 * have a method that processes a single file.
 * 
 * The student should verify that each smaller step works before
 * continuing to code the next part of the larger problem.
 */
package src.main.java;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class Zipf {
    public File file;
    /**
     * Read all the *.txt files in the folder.
     * The words will be normalized (no punctuation, or numbers,
     * and must be lower-case).
     * 
     * @param folder the File object representing the folder to process
     * @return a sorted List<WordFreq> ready for plotting meaning
     *         that it is sorted by frequency.
     */
    public List<WordFreq> processFolder(File folder) {
        // TODO: implement this method (Do not change this method signature)
        Map<String, Integer> zipfmap = new HashMap();
        if(!folder.isDirectory()){
            throw new IllegalArgumentException();
        }
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            File file = listOfFiles[i];
            if (file.isFile() && file.getPath().endsWith(".txt")) {
                processFile(zipfmap, file.getPath());
              /* do somthing with content */
            } 
        }
        // List<WordCount> wordcounts = zipfmap.entrySet().stream().map(e -> new WordCount(e.getKey(), e.getValue())).sorted().collect(Collectors.toList());
        // double gFreq = (double) wordcounts.get(0).getNumValue();
        // List<WordFreq> wordFreqs = wordcounts.stream().map(e -> new WordFreq(e.getWord(), e.getNumValue()/gFreq)).collect(Collectors.toList());
        // double gFreq = (double) Collections.max(zipfmap.values());
        List<WordFreq> wordFreqs = zipfmap.entrySet().stream().map(e -> new WordFreq(e.getKey(), (double) e.getValue())).sorted().collect(Collectors.toList());
        for (int x = 0; x < wordFreqs.size(); x++){
            String word = wordFreqs.get(x).getWord();
            double freq = wordFreqs.get(x).getFreq();
            wordFreqs.set(x, new WordFreq(word, (freq / (freq * (x + 1)))));
        }
        return wordFreqs;
    }

    /**
     * Process a single file and updated the map accordingly.
     * The words will be normalized (no punctuation, or numbers,
     * and must be lower-case; use a regex).
     * 
     * @param words    A Map that is updated by the words in the file
     * @param filename The name of a file to be processed
     */
    public void processFile(Map<String, Integer> words, String filename) {
        try {
            String regex = "\\b\\w+\\b";
            String stuff = Files.readString(Path.of(filename)).replaceAll("<[^>]+>", "");
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(stuff);
            while (matcher.find()){
                String data = matcher.group().toLowerCase();
                if (words.get(data) == null) words.put(data, 1);
                else if (words.get(data) >= 1) words.replace(data, words.get(data) + 1);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Unfortunately, the file does not exist(says in a british accent)");
        }
    }
}
