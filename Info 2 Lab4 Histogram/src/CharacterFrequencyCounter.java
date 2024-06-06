import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class CharacterFrequencyCounter {
    public static void main(String[] args) {

        testCount();


        testFindMostFrequentCharacter();
    }


    public static int[] count(Reader reader) {
        int[] charFrequencies = new int[26 * 2];
        int totalCharacters = 0;
        try (BufferedReader br = new BufferedReader(reader)) {
            int nextChar;
            while ((nextChar = br.read()) != -1) {
                char character = (char) nextChar;
                if (Character.isLetter(character)) {
                    totalCharacters++;
                    if (character >= 'a' && character <= 'z') {
                        charFrequencies[character - 'a']++;
                    } else if (character >= 'A' && character <= 'Z') {
                        charFrequencies[character - 'A' + 26]++;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("An error occurred while reading the input: " + e.getMessage());
        }
        for (int i = 0; i < charFrequencies.length; i++) {
            charFrequencies[i] = (int) Math.round((double) charFrequencies[i] / totalCharacters * 100);
        }
        return charFrequencies;
    }

    public static char findMostFrequentCharacter(int[] charFrequencies) {
        int maxFrequency = 0;
        char mostFrequentChar = ' ';
        for (char c = 'a'; c <= 'z'; c++) {
            if (charFrequencies[c - 'a'] > maxFrequency) {
                maxFrequency = charFrequencies[c - 'a'];
                mostFrequentChar = c;
            }
        }
        for (char c = 'A'; c <= 'Z'; c++) {
            if (charFrequencies[c - 'A' + 26] > maxFrequency) {
                maxFrequency = charFrequencies[c - 'A' + 26];
                mostFrequentChar = c;
            }
        }
        return mostFrequentChar;
    }

    public static void printHistogram(int[] charFrequencies, int width) {
        System.out.println("Histogram of Character Frequencies:");
        for (char c = 'a'; c <= 'z'; c++) {
            System.out.print(c + ": ");
            printBar(charFrequencies[c - 'a'], width);
            System.out.println();
        }
        for (char c = 'A'; c <= 'Z'; c++) {
            System.out.print(c + ": ");
            printBar(charFrequencies[c - 'A' + 26], width);
            System.out.println();
        }
    }

    private static void printBar(int frequency, int width) {
        int barWidth = (int) Math.ceil((double) frequency / 100 * width);
        for (int i = 0; i < barWidth; i++) {
            System.out.print("-");
        }
    }

    public static void testCount() {

        String testString = "Hello, World!";
        System.out.println("Test Case 1:");
        System.out.println("Input String: " + testString);
        int[] frequencies = count(new StringReader(testString));
        System.out.println("Character Frequencies:");
        printFrequencies(frequencies);
        System.out.println("Histogram:");
        printHistogram(frequencies, 20);


        try (BufferedReader fileReader = new BufferedReader(new FileReader("./src/text.txt"))) {
            System.out.println("\nTest Case 2:");
            System.out.println("Character Frequencies from File:");
            frequencies = count(fileReader);
            printFrequencies(frequencies);
            System.out.println("Histogram:");
            printHistogram(frequencies, 20);
        } catch (IOException e) {
            System.err.println("An error occurred while reading the file: " + e.getMessage());
        }
    }


    public static void testFindMostFrequentCharacter() {
        String testString = "Hello, World!";
        System.out.println("\nTest Case for findMostFrequentCharacter():");
        System.out.println("Input String: " + testString);
        int[] frequencies = count(new StringReader(testString));
        System.out.println("Most frequent character: " + findMostFrequentCharacter(frequencies));


        try (BufferedReader fileReader = new BufferedReader(new FileReader("./src/text.txt"))) {
            System.out.println("\nTest Case for findMostFrequentCharacter():");
            System.out.println("Most frequent character from File:");
            frequencies = count(fileReader);
            System.out.println("Most frequent character: " + findMostFrequentCharacter(frequencies));
        } catch (IOException e) {
            System.err.println("An error occurred while reading the file: " + e.getMessage());
        }
    }


    public static void printFrequencies(int[] frequencies) {
        for (char c = 'a'; c <= 'z'; c++) {
            System.out.println(c + ": " + frequencies[c - 'a']);
        }
        for (char c = 'A'; c <= 'Z'; c++) {
            System.out.println(c + ": " + frequencies[c - 'A' + 26]);
        }
    }
}
