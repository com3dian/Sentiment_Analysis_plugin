package com.europaweg.sentimentanalysis_plugin.commentDetection;

import com.europaweg.sentimentanalysis_plugin.commentDetection.Detector;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DetectorTest {

    @Test
    void testProcessText() {
        String inputText = "// This is a test\nwith multiple // comments\nand line breaks /* in between */.";
        String processedText = Detector.processText(inputText);
        assertEquals(" This is a test with multiple comments and line breaks in between .", processedText);
    }

    @Test
    void testRemoveIndentation() {
        String kotlinString = "   val x = 10\n" +
                "   val y = 20\n" +
                "   val z = 30\n";
        String result = Detector.removeIndentation(kotlinString);
        assertEquals("val x = 10\nval y = 20\nval z = 30", result);
    }

    @Test
    void testCheckString() {
        ArrayList<ArrayList<Integer>> allStringsLocation = new ArrayList<>();
        ArrayList<Integer> innerList = new ArrayList<>();
        innerList.add(5);
        innerList.add(15);
        allStringsLocation.add(innerList);

        boolean result = Detector.checkString(allStringsLocation, 8, 12);
        assertEquals(true, result);
    }

    @Test
    void testGetAllStrings() {
        String kotlinCode = "val string1 = \"This is a string\"\n" +
                "val string2 = 'Another string'\n" +
                "/* Multi-line\n" +
                "   // comment */\n";
        ArrayList<ArrayList<Integer>> result = Detector.getAllStrings(kotlinCode);
        assertEquals(2, result.size());
    }

    @Test
    void testExtractCommentsFromKotlinCode() {
        String kotlinCode = "// This is a /* single-line */ comment\n" +
                "val x = 10 // This is an inline comment\n" +
                "/* Multi-line\n" +
                "   // comment */\n" +
                "val y = 20 // Another inline comment\n";
        ArrayList<String> extractedComments = Detector.extractCommentsFromKotlinCode(kotlinCode, false);

        assertEquals(4, extractedComments.size());
        assertEquals(" This is a single-line comment ", extractedComments.get(0));
        assertEquals(" This is an inline comment", extractedComments.get(1));
        assertEquals(" Multi-line comment ", extractedComments.get(2));
        assertEquals(" Another inline comment", extractedComments.get(3));
    }

    @Test
    void testHandleNewlines() {
        String input = "\nThis is a test\nwith leading and trailing newlines\n";
        String result = Detector.handleNewlines(input);
        assertEquals("  This is a test\n  with leading and trailing newlines", result);
    }
}
