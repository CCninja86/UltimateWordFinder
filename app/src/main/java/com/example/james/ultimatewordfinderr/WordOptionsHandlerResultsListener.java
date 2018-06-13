package com.example.james.ultimatewordfinderr;

import java.util.ArrayList;

import nz.co.ninjastudios.datamuseandroid.Word;

public interface WordOptionsHandlerResultsListener {

    void onSynonymsSuccess(String word, ArrayList<Word> synonyms);

    void onDefinitionsSuccess(String word, DefinitionList definitionList);
}
