package test.java;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
// import java.util.List;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import src.main.java.WordCount;
import src.main.java.WordFreq;
// import src.main.java.WordCount;
import src.main.java.Zipf;

class Tests {

    // NOTE: These are sample test cases. 

    @Test
    void testProcessBadFile() {
        Zipf zipf = new Zipf();
        String fileName = "./small/badfile.txt";

        HashMap<String, Integer> map = new HashMap<>();

        // TODO: Implement this test so it no longer fails
        assertThrows(IllegalArgumentException.class, () -> {
            zipf.processFile(map, fileName);
        });
    }

    @Test
    void testProcessBadFolder() {
        Zipf zipf = new Zipf();

        // TODO: Implement this test so it no longer fails
        assertThrows(IllegalArgumentException.class, () -> {
            File file = new File("./badfolder");
            zipf.processFolder(file);
        });
    }

    @Test
    void testProcessFolder() {
        Zipf zipf = new Zipf();
        assertNotNull(zipf.processFolder(new File("./small")));
    }
    @Test
  void testProcessFile() throws FileNotFoundException {
    Zipf zipf = new Zipf();
    String fileName = "./testthing/fortesting.txt";
    HashMap<String, Integer> expectedMap = new HashMap<>();
    expectedMap.put("the", 3);
    expectedMap.put("this", 2);
    expectedMap.put("is", 2);
    expectedMap.put("a", 1);
    expectedMap.put("sample", 1);
    expectedMap.put("text", 1);
    expectedMap.put("file", 1);
    HashMap<String, Integer> actualMap = new HashMap<>();
    zipf.processFile(actualMap, fileName);
    assertEquals(expectedMap, actualMap);
  }

  @Test
  void testProcessFolderList() throws FileNotFoundException {
    Zipf zipf = new Zipf();
    File folder = new File("./testthing");
    List<WordCount> wordCounts = zipf.processFolder(folder).stream().map(wf -> new WordCount(wf.getWord(), (int) wf.getFreq())).collect(Collectors.toList());
    List<WordCount> expectedWordCounts = new ArrayList<>(Arrays.asList(
        new WordCount("the", 1),
        new WordCount("is", 0),
        new WordCount("this", 0),
        new WordCount("a", 0),
        new WordCount("file", 0),
        new WordCount("sample", 0),
        new WordCount("text", 0)
    ));
    assertEquals(expectedWordCounts, wordCounts);
  }

  @Test
  void testProcessFolderListSorted() throws FileNotFoundException {
    Zipf zipf = new Zipf();
    File folder = new File("./testthing");
    List<WordCount> wordCounts = zipf.processFolder(folder).stream().map(wf -> new WordCount(wf.getWord(), (int) wf.getFreq())).sorted().collect(Collectors.toList());
    List<WordCount> expectedWordCounts = new ArrayList<>(Arrays.asList((
        new WordCount("the", 1)),
        new WordCount("a", 0),
        new WordCount("file", 0),
        new WordCount("is", 0),
        new WordCount("sample", 0),
        new WordCount("text", 0),
        new WordCount("this", 0)
    ));
    assertEquals(expectedWordCounts, wordCounts);
  }

  @Test
  void testWordCountGettersSetters() {
    WordCount wordCount = new WordCount("test", 5);
    assertEquals("test", wordCount.getWord());
    assertEquals(5, wordCount.getNumValue());
    wordCount.setNumValue(10);
    assertEquals(10, wordCount.getNumValue());
  }

  @Test
  void testWordCountToString() {
WordCount wordCount = new WordCount("test", 5);
    assertEquals("test : 5", wordCount.toString());
  }

  @Test
  void testWordCountCompareTo() {
    WordCount wordCount1 = new WordCount("test", 5);
    WordCount wordCount2 = new WordCount("test", 10);
    assertTrue(wordCount1.compareTo(wordCount2) > 0);
    assertTrue(wordCount2.compareTo(wordCount1) < 0);
    WordCount wordCount3 = new WordCount("test", 5);
    assertEquals(0, wordCount1.compareTo(wordCount3));
    WordCount wordCount4 = new WordCount("test2", 5);
    assertTrue(wordCount1.compareTo(wordCount4) < 0);
  }

  @Test
  void testWordFreqGetters() {
    WordFreq wordFreq = new WordFreq("test", 0.5);
    assertEquals("test", wordFreq.getWord());
    assertEquals(0.5, wordFreq.getFreq());
  }

  @Test
  void testWordFreqToString() {
    WordFreq wordFreq = new WordFreq("test", 0.5);
    assertEquals("        test : 0.50", wordFreq.toString());
  }

  @Test
  void testWordFreqCompareTo() {
    WordFreq wordFreq1 = new WordFreq("test", 0.5);
    WordFreq wordFreq2 = new WordFreq("test", 0.6);
    assertTrue(wordFreq1.compareTo(wordFreq2) > 0);
    assertTrue(wordFreq2.compareTo(wordFreq1) < 0);
    WordFreq wordFreq3 = new WordFreq("test", 0.5);
    assertEquals(0, wordFreq1.compareTo(wordFreq3));
    WordFreq wordFreq4 = new WordFreq("test2", 0.5);
    assertTrue(wordFreq1.compareTo(wordFreq4) < 0);
  }

  @Test
    void testInvalidFile() {
        Zipf zipf = new Zipf();
        Map<String, Integer> wordCounts = new HashMap<>();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            zipf.processFile(wordCounts, "invalid_file.txt");
        });

        assertTrue(exception.getMessage().contains("NO FILE SENOR"), "Error message should indicate file issue");
    }

    @Test
    void testEmptyFile() throws IOException {
        String emptyFile = "test_data/empty.txt";
        new File(emptyFile).createNewFile();

        Zipf zipf = new Zipf();
        Map<String, Integer> wordCounts = new HashMap<>();
        zipf.processFile(wordCounts, emptyFile);

        assertTrue(wordCounts.isEmpty(), "Word counts should be empty for an empty file");

        new File(emptyFile).delete();
    }

    @Test
    void testPunctuationHandling() {
        String punctFilePath = "test_data/punct.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(punctFilePath))) {
            writer.write("word, word.word! word?");
        } catch (IOException e) {
            fail("Failed to write punctuation test file.");
        }

        Zipf zipf = new Zipf();
        Map<String, Integer> wordCounts = new HashMap<>();
        zipf.processFile(wordCounts, punctFilePath);

        assertEquals(4, wordCounts.get("word"), "Punctuation should not affect word counts");

        new File(punctFilePath).delete();
    }
}
