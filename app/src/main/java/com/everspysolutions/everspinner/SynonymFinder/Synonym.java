package com.everspysolutions.everspinner.SynonymFinder;

public class Synonym {
    private String word;
    private int score;

    public Synonym(String word, int score) {
        this.word = word;
        this.score = score;
    }

    public String getWord() {
        return word;
    }
    public int getScore() {
        return score;
    }
}
