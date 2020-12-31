package com.everspysolutions.everspinner.TextSpinner;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

public class TextSpinnerTest {

    private static final long seed = 0;

    // Selection Solving
    // Simple single choice
    @Test
    public void SolveSimpleSingle(){
        String testCase = "{dog}";
        String result = (new TextSpinner(seed)).solveSelections(testCase);
        Assert.assertEquals("dog", result);
    }

    @Test
    public void SolveSimpleMultiple(){
        String testCase = "{dog|dog|dog}";
        String result = (new TextSpinner(seed)).solveSelections(testCase);
        Assert.assertEquals("dog", result);
    }

    @Test
    public void SolveSimpleNested(){
        String testCase = "{dog|{dog|dog}}";
        String result = (new TextSpinner(seed)).solveSelections(testCase);
        Assert.assertEquals("dog", result);
    }

    @Test
    public void SolveSentenceSimple(){
        String testCase = "The quick brown fox jumps over the lazy dog";
        String result = (new TextSpinner(seed)).solveSelections(testCase);
        String qbf = "The quick brown fox jumps over the lazy dog";
        Assert.assertEquals(qbf, result);
    }

    @Test
    public void SolveSentenceSingle(){
        String testCase = "The quick brown fox jumps over the lazy {dog}";
        String result = (new TextSpinner(seed)).solveSelections(testCase);
        String qbf = "The quick brown fox jumps over the lazy dog";
        Assert.assertEquals(qbf, result);
    }

    @Test
    public void SolveSimpleSentenceMultiple(){
        String testCase = "The quick brown {fox} jumps over the lazy {dog}";
        String result = (new TextSpinner(seed)).solveSelections(testCase);
        String qbf = "The quick brown fox jumps over the lazy dog";
        Assert.assertEquals(qbf, result);
    }

    @Test
    public void SolveSimpleSentenceNested(){
        String testCase = "The quick brown {fox|{fox}} jumps over the lazy {dog}";
        String result = (new TextSpinner(seed)).solveSelections(testCase);
        String qbf = "The quick brown fox jumps over the lazy dog";
        Assert.assertEquals(qbf, result);
    }

    // Simple multiple choice
    @Test
    public void SolveMultiple(){
        String testCase = "{dog|cat|mouse}";
        String result = (new TextSpinner(seed)).solveSelections(testCase);
        Assert.assertEquals("dog", result);
    }

    @Test
    public void SolveNested(){
        String testCase = "{mouse|{cat|dog}}";
        String result = (new TextSpinner(seed)).solveSelections(testCase);
        Assert.assertEquals("dog", result);
    }

    @Test
    public void SolveSentenceMultiple(){
        String testCase = "The quick brown {fox|mouse} jumps over the lazy {dog|cat}";
        String result = (new TextSpinner(seed)).solveSelections(testCase);
        String qbf = "The quick brown mouse jumps over the lazy cat";
        Assert.assertEquals(qbf, result);
    }

    @Test
    public void SolveSentenceNested(){
        String testCase = "The quick brown {fox|{rat|cat}} jumps over the lazy {dog}";
        String result = (new TextSpinner(seed)).solveSelections(testCase);
        String qbf = "The quick brown cat jumps over the lazy dog";
        Assert.assertEquals(qbf, result);
    }

    // Passing test cases
    @Test
    public void errorCheckZeroPass() {
        String testCase = "The quick brown fox jumps over the lazy dog";
        int result = TextSpinner.errorCheckText(testCase);
        Assert.assertEquals(0, result);
    }
    @Test
    public void errorCheckSimplePass(){
        String testCase = "The {quick|fast} brown fox jumps over the lazy dog";
        int result = TextSpinner.errorCheckText(testCase);
        Assert.assertEquals(1, result);
    }

    @Test
    public void errorCheckMultiplePass(){
        String testCase = "The {quick|fast} brown fox jumps over the {lazy|sleeping|tired} dog";
        int result = TextSpinner.errorCheckText(testCase);
        Assert.assertEquals(2, result);
    }

    @Test
    public void errorCheckNestedPass(){
        String testCase = "The quick brown fox jumps over the {lazy|{sleeping|{tired|small}}} dog";
        int result = TextSpinner.errorCheckText(testCase);
        Assert.assertEquals(3, result);
    }

    // Unmatched closing test cases
    @Test
    public void errorCheckSimpleUnmatchedClosed(){
        String testCase = "The quick|fast} brown fox jumps over the lazy dog";
        int result = TextSpinner.errorCheckText(testCase);
        Assert.assertEquals(-1, result);
    }

    @Test
    public void errorCheckMultipleUnmatchedClosed(){
        String testCase = "The {quick|fast} brown fox jumps over the {lazy|sleeping}|tired} dog";
        int result = TextSpinner.errorCheckText(testCase);
        Assert.assertEquals(-1, result);
    }

    @Test
    public void errorCheckCountPreventionUnmatchedClosed(){
        String testCase = "The }{quick|fast brown {fox|cat} jumps over the lazy dog";
        int result = TextSpinner.errorCheckText(testCase);
        Assert.assertEquals(-1, result);
    }
    // Unmatched opening test cases
    @Test
    public void errorCheckSimpleUnmatchedOpen(){
        String testCase = "The {quick|fast brown fox jumps over the lazy dog";
        int result = TextSpinner.errorCheckText(testCase);
        Assert.assertEquals(-2, result);
    }

    @Test
    public void errorCheckMultipleUnmatchedOpen(){
        String testCase = "The {quick|{fast|rapid} brown fox jumps over the lazy dog";
        int result = TextSpinner.errorCheckText(testCase);
        Assert.assertEquals(-2, result);
    }
}