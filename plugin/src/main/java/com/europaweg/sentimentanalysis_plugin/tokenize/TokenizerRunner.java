package com.europaweg.sentimentanalysis_plugin.tokenize;

import com.genesys.roberta.tokenizer.RobertaTokenizer;
import com.genesys.roberta.tokenizer.RobertaTokenizerResources;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class TokenizerRunner {
    private final RobertaTokenizer robertaTokenizer;

    public TokenizerRunner(String vocabularyBaseDirPath) {
        // Create tokenizer resources
        RobertaTokenizerResources robertaResources = new RobertaTokenizerResources(vocabularyBaseDirPath);

        // Create the tokenizer
        this.robertaTokenizer = new RobertaTokenizer(robertaResources);
    }

    public long[] tokenizeSentence(String sentence) {
        return robertaTokenizer.tokenize(sentence);
    }

    public ArrayList<long[]> tokenizeParagraph(ArrayList<String> paragraph) {
        ArrayList<long[]> tokenizedSentences = new ArrayList<>();
        for (String sentence : paragraph) {
            long[] tokens = tokenizeSentence(sentence);
            tokenizedSentences.add(tokens);
        }
        return tokenizedSentences;
    }

    public ArrayList<long[]> padSequences(ArrayList<long[]> sequences) {
        // Find the maximum length among the sequences
        int maxLength = sequences.stream().mapToInt(tokens -> tokens.length).max().orElse(0);

        // Pad each sequence to match the maximum length
        ArrayList<long[]> paddedSequences = new ArrayList<>();
        for (long[] sequence : sequences) {
            long[] paddedTokens = Arrays.copyOf(sequence, maxLength);
            Arrays.fill(paddedTokens, sequence.length, maxLength, 1); // Padding with token 1
            paddedSequences.add(paddedTokens);
        }
        return paddedSequences;
    }

    // Get the shape of a list of padded tokenized sentences
    public static long[] getPaddedShape(ArrayList<long[]> paddedSentences) throws IllegalArgumentException {
        if (paddedSentences.isEmpty()) {
            throw new IllegalArgumentException("Input list is empty.");
        }

        int numSentences = paddedSentences.size();
        int maxLength = paddedSentences.get(0).length;

        // Check if the input list is properly padded
        for (long[] tokens : paddedSentences) {
            if (tokens.length != maxLength) {
                throw new IllegalArgumentException("Input list is not properly padded.");
            }
        }

        return new long[]{numSentences, maxLength};
    }

    public static long[] flatten(ArrayList<long[]> nestedList) {
        // Calculate the total length of the flattened array
        int totalLength = nestedList.stream().mapToInt(array -> array.length).sum();

        // Create the flattened array
        long[] flattenedArray = new long[totalLength];

        // Copy elements from each array to the flattened array
        int currentIndex = 0;
        for (long[] array : nestedList) {
            System.arraycopy(array, 0, flattenedArray, currentIndex, array.length);
            currentIndex += array.length;
        }

        return flattenedArray;
    }

    public static void main(String[] args) {
        // Example usage
        String vocabularyBaseDirPath = "/home/com3dian/IdeaProjects/SentimentAnalysis_Plugin/src/main/resources/tokenizer";
        TokenizerRunner tokenizerRunner = new TokenizerRunner(vocabularyBaseDirPath);

        // Example sentences
        ArrayList<String> exampleSentences = new ArrayList<>();
        exampleSentences.add("erererererererererererererererererererererererer");
        exampleSentences.add("lower newer");
        exampleSentences.add("er");
        exampleSentences.add("");

        // Tokenize and print results
        ArrayList<long[]> tokenizedSentences = tokenizerRunner.tokenizeParagraph(exampleSentences);
        ArrayList<long[]> paddedSentences = tokenizerRunner.padSequences(tokenizedSentences);
        long[] flattenedList = flatten(paddedSentences);
        long[] shape = getPaddedShape(paddedSentences);

        for (int i = 0; i < paddedSentences.size(); i++) {
            printTokens("Sentence " + (i + 1), paddedSentences.get(i));
        }
        printTokens("flattened array: ", flattenedList);
        System.out.print("shape: " + shape[0] + " " + shape[1]);
    }

    private static void printTokens(String title, long @NotNull [] tokens) {
        System.out.println(title + ": ");
        for (long token : tokens) {
            System.out.print(token + " ");
        }
        System.out.println("\n");
    }
}

