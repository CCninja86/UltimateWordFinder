package com.example.james.ultimatewordfinderr;

/**
 * Created by James on 1/05/2016.
 */
public class Globals {
    private static Globals instance;
    private Dictionary dictionary;
    private Scrabble game;
    private boolean wordOptionsHintShown = false;

    private Globals(){

    }

    public void setDictionary(Dictionary dictionary){
        this.dictionary = dictionary;
    }

    public Dictionary getDictionary(){
        return this.dictionary;
    }

    public void setGame(Scrabble game){
        this.game = game;
    }

    public Scrabble getGame(){
        return  this.game;
    }

    public static synchronized Globals getInstance(){
        if(instance == null){
            instance = new Globals();
        }

        return instance;
    }

    public boolean isWordOptionsHintShown() {
        return wordOptionsHintShown;
    }

    public void setWordOptionsHintShown(boolean wordOptionsHintShown) {
        this.wordOptionsHintShown = wordOptionsHintShown;
    }
}
