package com.europaweg.sentimentanalysis_plugin.commentDetection;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Detector {

    public static String processText(String inputText) {

        // Replace line breaks or comment symbol with spaces
        String withoutCommentSymbol = inputText.replaceAll("//|/\\*|\\*/|\\r?\\n", " ");

        // Condense multiple spaces into one space and return
        return withoutCommentSymbol.replaceAll("\\s+", " ");
    }

    public static String removeIndentation(String kotlinString) {
        StringBuilder result = new StringBuilder();

        String[] lines = kotlinString.split("\n");
        for (String line : lines) {
            // Remove leading whitespaces from each line
            String trimmedLine = line.replaceAll("^\\s+", "");
            result.append(trimmedLine).append("\n");
        }

        // Remove the trailing newline character added in the last iteration
        return result.toString().trim();
    }

    public static boolean checkString(ArrayList<ArrayList<Integer>> allStringsLocation, int start, int end) {

        for (ArrayList<Integer> innerList : allStringsLocation) {
            int stringStart = innerList.get(0);
            int stringEnd = innerList.get(1);

            if (start >= stringStart&&start <= stringEnd) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<ArrayList<Integer>> getAllStrings(String kotlinCode) {
        ArrayList<ArrayList<Integer>> listOfLists = new ArrayList<>();

        String regex = "((?s)\"\"\".*?\"\"\")|((?s)'''.*?''')|\"[^\"\n]*\"|'[^'\n]*'";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(kotlinCode);
        while (matcher.find()) {
            ArrayList<Integer> sublist = new ArrayList<>();
            sublist.add(matcher.start());
            sublist.add(matcher.end());
            listOfLists.add(sublist);
        }
        return listOfLists;
    }


    public static ArrayList<String> extractCommentsFromKotlinCode(String kotlinCode, boolean isTest) {
        // Regular expression to match Kotlin block comments (/* ... */) and
        // block of single line comments (// ... \n// .. )
        String indentationRemovedCode = "\n" + removeIndentation(kotlinCode) + "\n";
        String regex = "((?s)(\\n)//.*?\\n+(?!(//)).*?)|(//.*)|((?s)/\\*.*?\\*/)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(indentationRemovedCode);

        ArrayList<ArrayList<Integer>> allStrings = getAllStrings(indentationRemovedCode);
        ArrayList<String> commentsList = new ArrayList<>();

        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            if(checkString(allStrings, start, end)){
                continue;
            }
            String comment = matcher.group();
            if (isTest) {
                commentsList.add(handleNewlines(comment));
            } else {
                commentsList.add(processText(comment));
            }
        }
        return commentsList;
    }

    public static String handleNewlines(String input) {
        // Remove leading newlines
        while (input.startsWith("\n")) {
            input = input.substring(1);
        }

        // Remove trailing newlines
        while (input.endsWith("\n")) {
            input = input.substring(0, input.length() - 1);
        }
        return "  " + input.replaceAll("\n", "\n  ");
    }


    public static void main(String[] args) {
        // Example usage
        String inputText = "// This is a text with\nline breaks, comment symbols\n /*and     multiple    spaces.*/";
        String processedText = processText(inputText);

        System.out.println("Original Text:\n" + inputText);
        System.out.println("\nProcessed Text:\n" + processedText);

        // Example Kotlin code
        String kotlinCode = "// This is a /* single-line */ comment\n" +
                "// This is a single-line comment\n" +
                "/* This is a block comment */\n" +
                "//This is a \n" +
                "// block comment\n" +
                "\n" +
                "    // // This is another single-line comment\n" +
                "val x = 10 // This is an inline comment\n" +
                "/* Multi-line\n" +
                "   // comment */\n" +
                "val y = 20 // Another inline comment\n" +
                "    // Continuous single-line comments\n" +
                "    // without line break\n" +
                " ' // comment in String ' " +
                " \" comment in multi-line String \" " +
                " // 'String in comment ' \n" +
                " \" /* com \" ment \n in multi-line \" String */ \" \n" +
                " \"\"\"\n /* com \" ment \n in multi-line \" String */ \n\"\"\" " +
                " /* com \" ment in multi-line \" \nString */ \n" ;

        // Extract comments from Kotlin code
        ArrayList<String> extractedComments = extractCommentsFromKotlinCode(kotlinCode, true);

        // Print extracted comments
        System.out.println("\n\nExtracted Comments:");
        for (String comment : extractedComments) {
            System.out.println("Next comment:" + "--------------\n" + comment + "\n");
        }
    }
}
