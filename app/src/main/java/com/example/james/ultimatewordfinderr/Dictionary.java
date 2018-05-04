package com.example.james.ultimatewordfinderr;


import android.app.ProgressDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by James on 18/11/2015.
 */
public class Dictionary implements Serializable {
    private ArrayList<Word> wordList;
    private ArrayList<String> stringWordList;
    private Map<String, Word> wordMap;
    private DatabaseHandler database;
    CSVReader csvReader;

    /**'
     * Creates a new Dictionary object with the list of words from the text file (from the database.txt once done transferring)
     * @throws java.io.IOException
     */
    public Dictionary() {
        this.wordList = new ArrayList<>();
        this.stringWordList = new ArrayList<>();
        this.wordMap = new HashMap<String, Word>();
    }

    /**
     * Returns the list of words
     * @return the list of words
     */
    public ArrayList<Word> getWordList() {
        return this.wordList;
    }

    private void populateStringWordList(){
        for(Word word : this.wordList){
            stringWordList.add(word.getWord());
        }
    }

    private void createWordMap(){
        for(Word word : this.wordList){
            this.wordMap.put(word.getWord(), word);
        }
    }

    public ArrayList<String> getStringWordList(){
        return this.stringWordList;
    }

    public ArrayList<Word> getWords(String letter, int position, int length){
        ArrayList<Word> wordsToReturn = new ArrayList<>();

        for(Word word : this.wordList){
            if((word.getWord().indexOf(letter) == position) && word.getWord().length() == length){
                wordsToReturn.add(word);
            }
        }

        return wordsToReturn;
    }

    public ArrayList<Word> getWordsOfLength(int length){
        ArrayList<Word> wordsToReturn = new ArrayList<>();

        for(Word word : this.wordList){
            if(word.getWord().length() == length){
                wordsToReturn.add(word);
            }
        }

        return wordsToReturn;
    }

    public ArrayList<Word> getWordsStartingWith(String prefix){
        ArrayList<Word> wordsToReturn = new ArrayList<>();

        for(Word word : this.wordList){
            if(word.getWord().startsWith(prefix)){
                wordsToReturn.add(word);
            }
        }

        return wordsToReturn;
    }

    public ArrayList<String> getStringWordsStartingWith(String prefix){
        ArrayList<String> wordsToReturn = new ArrayList<>();

        for(String word : this.stringWordList){
            if(word.startsWith(prefix)){
                wordsToReturn.add(word);
            }
        }

        return wordsToReturn;
    }


    public ArrayList<Word> getWordsEndingWith(String suffix){
        ArrayList<Word> wordsToReurn = new ArrayList<>();

        for(Word word : this.wordList){
            if(word.getWord().endsWith(suffix)){
                wordsToReurn.add(word);
            }
        }

        return wordsToReurn;
    }

    public void addWord(Word word){
        this.wordList.add(word);
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
        //boolean isOfficial = false;

        /*for(Word wordInDictionary : this.wordList){
            if(wordInDictionary.getWord().equals(word)){
                isOfficial = wordInDictionary.isWordOfficial();
            }
        }*/

        return this.wordMap.get(word).isWordOfficial();
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

    public void linkCSVReader(CSVReader csvReader){
        this.csvReader = csvReader;
    }

    public void unlinkDatabase(){
        this.database = null;
    }

    public void setWordList(ProgressDialog progressDialog){
        this.wordList = this.csvReader.readFile("words.csv", progressDialog);
        populateStringWordList();
        createWordMap();
    }
}
