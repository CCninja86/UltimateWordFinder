package com.example.james.ultimatewordfindernew;

import java.io.Serializable;

/**
 * Created by James on 11/11/2015.
 */
public class Tile implements Serializable {
    private String letter;
    private int points;
    private boolean vowel;
    private int quantity;

    public Tile(String letter, int points, boolean vowel, int quantity) {
        this.letter = letter;
        this.points = points;
        this.vowel = vowel;
        this.quantity = quantity;
    }

    public String getLetter() {
        return this.letter;
    }

    public int getPoints() {
        return this.points;
    }

    public boolean isVowel() {
        return this.vowel;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void removeTile(){
        this.quantity--;
    }

    public boolean enoughTiles() {
        boolean enoughTiles = false;

        if (this.quantity > 0) {
            enoughTiles = true;
        }

        return enoughTiles;
    }

    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
