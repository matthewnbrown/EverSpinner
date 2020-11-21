package com.everspysolutions.everspinner.SynonymFinder;

import java.io.Serializable;

public class Synonym implements Serializable {
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
    public void setScore(int score) { this.score = score; }
}
