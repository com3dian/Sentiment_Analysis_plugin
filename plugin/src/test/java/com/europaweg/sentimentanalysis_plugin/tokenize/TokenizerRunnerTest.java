package com.europaweg.sentimentanalysis_plugin.tokenize;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class TokenizerRunnerTest {

    private String tokenizerPath = "/home/com3dian/Downloads/roberta-tokenizer";

    @Test
    void tokenizeSentence() {
        String vocabularyBaseDirPath = tokenizerPath;
        TokenizerRunner tokenizerRunner = new TokenizerRunner(vocabularyBaseDirPath);

        String sentence = "Example sentence for testing";
        long[] tokens = tokenizerRunner.tokenizeSentence(sentence);

        assertNotNull(tokens);
        assertTrue(tokens.length > 0);
    }

    @Test
    void tokenizeParagraph() {
        String vocabularyBaseDirPath = tokenizerPath;
        TokenizerRunner tokenizerRunner = new TokenizerRunner(vocabularyBaseDirPath);

        ArrayList<String> paragraph = new ArrayList<>(Arrays.asList(
                "First sentence.",
                "Second sentence.",
                "Third sentence."
        ));

        ArrayList<long[]> tokenizedSentences = tokenizerRunner.tokenizeParagraph(paragraph);

        assertNotNull(tokenizedSentences);
        assertEquals(paragraph.size(), tokenizedSentences.size());

        for (long[] tokens : tokenizedSentences) {
            assertTrue(tokens.length > 0);
        }
    }

    @Test
    void padSequences() {
        ArrayList<long[]> sequences = new ArrayList<>(Arrays.asList(
                new long[]{1, 2, 3},
                new long[]{4, 5},
                new long[]{6, 7, 8, 9}
        ));

        TokenizerRunner tokenizerRunner = new TokenizerRunner(tokenizerPath);
        ArrayList<long[]> paddedSequences = tokenizerRunner.padSequences(sequences);

        assertNotNull(paddedSequences);

        int maxLength = paddedSequences.get(0).length;
        for (long[] tokens : paddedSequences) {
            assertEquals(maxLength, tokens.length);
        }
    }

    @Test
    void getPaddedShape() {
        ArrayList<long[]> paddedSentences = new ArrayList<>(Arrays.asList(
                new long[]{1, 2, 3},
                new long[]{4, 5, 6},
                new long[]{7, 8, 9}
        ));

        long[] shape = TokenizerRunner.getPaddedShape(paddedSentences);

        assertNotNull(shape);
        assertEquals(3, shape[0]); // Number of sentences
        assertEquals(3, shape[1]); // Max length
    }

    @Test
    void flatten() {
        ArrayList<long[]> nestedList = new ArrayList<>(Arrays.asList(
                new long[]{1, 2, 3},
                new long[]{4, 5},
                new long[]{6, 7, 8, 9}
        ));

        long[] flattenedArray = TokenizerRunner.flatten(nestedList);

        assertNotNull(flattenedArray);
        assertEquals(9, flattenedArray.length);
    }

    // You can add more tests based on your specific requirements.
}

