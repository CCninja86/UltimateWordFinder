package com.example.james.ultimatescrabbleapp;

/**
 * Created by James on 1/05/2016.
 */
public class Globals {
    private static Globals instance;
    private Dictionary dictionary;

    private Globals(){

    }

    public void setDictionary(Dictionary dictionary){
        this.dictionary = dictionary;
    }

    public Dictionary getDictionary(){
        return this.dictionary;
    }

    public static synchronized Globals getInstance(){
        if(instance == null){
            instance = new Globals();
        }

        return instance;
    }
}
