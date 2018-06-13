package com.example.james.ultimatewordfinderr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by James on 11/11/2015.
 */
public class Player implements Serializable {
    private String name;
    private int colour;
    private int score;
    private PlayerStatus status;
    private Scrabble scrabbleGame;
    private ArrayList<String> playerWordHistory;

    public Player(String name, int colour) {
        this.setName(name);
        this.score = 0;
        this.status = PlayerStatus.TIED;
        this.playerWordHistory = new ArrayList<>();
        this.colour = colour;
    }

    public String getName() {

        return this.name;
    }

    public int getScore() {
        return this.score;
    }

    public PlayerStatus getStatus() {
        return this.status;
    }

    public void addWordScore(String word, Map<String, Integer> wordsWithWordBonuses, boolean doubleLetter, boolean tripleLetter, boolean doubleWord, boolean tripleWord, ArrayList<String> doubleLetters, ArrayList<String> tripleLetters) {
        String[] letters = word.split("");
        ArrayList<Tile> tiles = this.getScrabbleGame().getTiles();
        ArrayList<String> doubleLetterBonuses = new ArrayList<>();
        ArrayList<String> tripleLetterBonuses = new ArrayList<>();
        int totalPoints = 0;

        if (doubleLetter) {
            doubleLetterBonuses = doubleLetters;
        }

        if (tripleLetter) {
            tripleLetterBonuses = tripleLetters;
        }

        for (int i = 0; i < letters.length; i++) {
            String letter = letters[i];

            if (!letter.equals("")) {
                Tile tile = scrabbleGame.getTileByLetter(letter);
                int letterPoints = tile.getPoints();

                if (doubleLetterBonuses.contains(tile.getLetter())) {
                    letterPoints *= 2;
                    doubleLetterBonuses.remove(tile.getLetter());
                } else if (tripleLetterBonuses.contains(tile.getLetter())) {
                    letterPoints *= 3;
                    tripleLetterBonuses.remove(tile.getLetter());
                }

                totalPoints += letterPoints;
            }
        }

        if (doubleWord) {
            if (wordsWithWordBonuses.containsKey(word.toLowerCase())) {
                if (wordsWithWordBonuses.get(word.toLowerCase()) == 2) {
                    totalPoints *= 2;
                }
            }
        }

        if (tripleWord) {
            if (wordsWithWordBonuses.containsKey(word.toLowerCase())) {
                if (wordsWithWordBonuses.get(word.toLowerCase()) == 3) {
                    totalPoints *= 3;
                }
            }
        }

        this.getPlayerWordHistory().add(word);
        this.setScore(this.score + totalPoints);
    }

    public void addCustomScore(int score) {
        this.setScore(this.score + score);
    }

    /**
     * @return the playerWordHistory
     */
    public ArrayList<String> getPlayerWordHistory() {
        return playerWordHistory;
    }

    /**
     * @param playerWordHistory the playerWordHistory to set
     */
    public void setPlayerWordHistory(ArrayList<String> playerWordHistory) {
        this.playerWordHistory = playerWordHistory;
    }

    public void updatePlayerWordHistory(String oldItem, String newItem) {
        int oldItemIndex = this.playerWordHistory.indexOf(oldItem);
        this.playerWordHistory.set(oldItemIndex, newItem);
    }

    /**
     * @param score the score to set
     */
    public void setScore(int score) {
        this.score = score;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColour() {
        return colour;
    }

    public void setColour(int colour) {
        this.colour = colour;
    }

    public Scrabble getScrabbleGame() {
        return scrabbleGame;
    }

    public void setScrabbleGame(Scrabble scrabbleGame) {
        this.scrabbleGame = scrabbleGame;
    }
}
