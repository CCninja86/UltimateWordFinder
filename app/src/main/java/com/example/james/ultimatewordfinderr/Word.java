package com.example.james.ultimatewordfinderr;

import java.io.Serializable;

/**
 * Created by James on 19/11/2015.
 */
public class Word implements Serializable {

    private int id;
    private String word;
    private int word_base_score;
    private boolean word_is_official;

    public Word(){

    }

    public Word(int id, String word, int word_base_score, boolean word_is_official){
        this.setId(id);
        this.setWord(word);
        this.setBaseScore(word_base_score);
        this.setWordIsOfficial(word_is_official);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getBaseScore() {
        return word_base_score;
    }

    public void setBaseScore(int word_base_score) {
        this.word_base_score = word_base_score;
    }

    public boolean isWordOfficial() {
        return word_is_official;
    }

    public void setWordIsOfficial(boolean word_is_official) {
        this.word_is_official = word_is_official;
    }
}
