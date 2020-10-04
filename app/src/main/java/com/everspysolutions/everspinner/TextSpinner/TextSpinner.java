package com.everspysolutions.everspinner.TextSpinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TextSpinner {

    /**
     * Spins a properly formatted string of text
     * @param text A string of text which is to be spun. Selections formatted similar to
     *            {A|B|C|{D|E}}
     * @return Text with randomly chosen selections
     */
    public static String solveSelections(String text) {
        StringBuilder sb = new StringBuilder();
        int cutStartPoint = 0;
        int i = 0;
        while (i < text.length()) {
            if (text.charAt(i) == '{') {
                sb.append(text.substring(cutStartPoint, i));
                int cutStartPoint2 = findClosingBracket(text, i);
                sb.append(solveSelection(text.substring(i, cutStartPoint2 + 1)));
                cutStartPoint = cutStartPoint2 + 1;
                i = cutStartPoint;
            }
            i++;
        }

        sb.append(text.substring(cutStartPoint));
        return sb.toString();
    }

    /**
     * Check a string of text for properly formatted selections
     * @param text Arbitrary string of text
     * @return -1 if there is an closing bracket unmatched with an open. -2 if there exists an
     * unclosed selection. Otherwise number of selections is returned
     */
    public static int errorCheckText(String text) {
        int openCount = 0;
        int closeCount = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '{') {
                openCount++;
            } else if (c == '}') {
                closeCount++;
                openCount--;
            }
            // A closed bracket is unmatched with an open {}}
            if (openCount < 0) {
                closeCount = -1;
                break;
            }
        }
        // Unclosed open brackets exist {}{
        if (openCount > 0) {
            closeCount = -2;
        }

        return closeCount;
    }

    /**
     * Solves a single selection
     * @param selection A selection formatted as {options seperated by '|'}
     * @return The chosen option
     */
    private static String solveSelection(String selection) {
        String selection2 = selection.substring(1, selection.length() - 1);
        List<String> options = new ArrayList<>();
        int lastPos = 0;
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < selection2.length()) {
            char c = selection2.charAt(i);
            if (c == '|') {
                sb.append(selection2.substring(lastPos, i));
                options.add(sb.toString());
                sb = new StringBuilder();
                lastPos = i + 1;
            } else if (c == '{') {
                sb.append(selection2.substring(lastPos, i));
                int lastPos2 = findClosingBracket(selection2, i);
                sb.append(solveSelection(selection2.substring(i, lastPos2 + 1)));
                i = lastPos2;
                lastPos = lastPos2 + 1;
            }
            i++;
        }
        sb.append(selection2.substring(lastPos));
        options.add(sb.toString());
        return chooseOption(options.toArray(new String[0]));
    }

    /**
     * Finds the closing curly bracket for a given selection.
     * @param text String of text that contains a selection
     * @param startPos Position of opening curly bracket in text
     * @return position of closing curly bracket in text
     */
    private static int findClosingBracket(String text, int startPos) {
        int endPos = startPos;
        int counter = 1;
        for (int i = startPos + 1; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '{') {
                counter++;
            }
            if (c == '}') {
                counter--;
            }
            if (counter == 0) {
                endPos = i;
                break;
            }
        }
        return endPos;
    }

    /**
     * Randomly chooses an option in an array of strings
     * @param options Array of possible choices
     * @return Chosen option
     */
    private static String chooseOption(String[] options) {
        Random rand = new Random();
        return options[rand.nextInt(options.length)];
    }
}
