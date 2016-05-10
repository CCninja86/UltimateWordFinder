package com.example.james.ultimatescrabbleapp;



import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by James on 18/11/2015.
 */
public class Dictionary implements Serializable {
    private ArrayList<Word> words;
    public static DatabaseHandler database;

    /**'
     * Creates a new Dictionary object with the list of words from the text file (from the database.txt once done transferring)
     * @throws java.io.IOException
     */
    public Dictionary() {
        this.words = new ArrayList<>();
    }

    /**
     * Returns the list of words
     * @return the list of words
     */
    public ArrayList<Word> getWordList() {
        return this.words;
    }

    /**
     * Returns the word at the specified index in the dictionary
     * @param index the index of the dictionary
     * @return the word at the specified index
     */
    public String getWordAtIndex(int index) {
        Object[] wordArray = this.words.toArray();

        return wordArray[index - 1].toString();
    }

    public ArrayList<Word> getWords(String letter, int position, int length){
        ArrayList<Word> wordsToReturn = new ArrayList<>();

        for(Word word : this.words){
            if((word.getWord().indexOf(letter) == position) && word.getWord().length() == length){
                wordsToReturn.add(word);
            }
        }

        return wordsToReturn;
    }

    public ArrayList<Word> getWordsOfLength(int length){
        ArrayList<Word> wordsToReturn = new ArrayList<>();

        for(Word word : this.words){
            if(word.getWord().length() == length){
                wordsToReturn.add(word);
            }
        }

        return wordsToReturn;
    }

    public ArrayList<Word> getWordsStartingWith(String prefix){
        ArrayList<Word> wordsToReturn = new ArrayList<>();

        for(Word word : this.words){
            if(word.getWord().startsWith(prefix)){
                wordsToReturn.add(word);
            }
        }

        return wordsToReturn;
    }

    public ArrayList<Word> getWordsEndingWith(String suffix){
        ArrayList<Word> wordsToReurn = new ArrayList<>();

        for(Word word : this.words){
            if(word.getWord().endsWith(suffix)){
                wordsToReurn.add(word);
            }
        }

        return wordsToReurn;
    }

    public void addWord(Word word){
        this.words.add(word);
    }

    /**
     * Returns the base word score for the specified word
     * @param word the word to get the base word score for
     * @return the base word score for the specified word
     */
    public int getBaseWordScore(String word) {
        int totalScore = 0;
        String[] letters = word.split("");

        for (String letter : letters) {
            if (!letter.equals("")) {
                int letterScore;
                letterScore = this.getLetterScore(letter);
                totalScore += letterScore;
            }
        }

        return totalScore;
    }

    public boolean isWordOfficial(String word){
        boolean isOfficial = false;

        for(Word wordInDictionary : this.words){
            if(wordInDictionary.getWord().equals(word)){
                isOfficial = wordInDictionary.isWordOfficial();
            }
        }

        return isOfficial;
    }

    /**
     * Returns the letter score for the specified letter
     * @param letter the letter to get the letter score for
     * @return the letter score for the specified letter
     */
    public int getLetterScore(String letter) {
        int score = 0;

        if("eaionrtlsu".contains(letter)){
            score = 1;
        } else if("dg".contains(letter)){
            score = 2;
        } else if("bcmp".contains(letter)){
            score = 3;
        } else if("fhvwy".contains(letter)){
            score = 4;
        } else if("k".contains(letter)){
            score = 5;
        } else if("jx".contains(letter)){
            score = 8;
        } else if("qz".contains(letter)){
            score = 10;
        }

        return score;
    }

    public void linkDatabase(DatabaseHandler database){
        this.database = database;
    }

    public void unlinkDatabase(){
        this.database = null;
    }

    public void setWordList(){
        this.words = this.database.getAllWords();
    }
}
