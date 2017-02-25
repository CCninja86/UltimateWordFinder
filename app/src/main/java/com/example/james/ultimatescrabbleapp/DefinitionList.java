package com.example.james.ultimatescrabbleapp;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by james on 4/09/2016.
 */
public class DefinitionList implements Serializable {

    ArrayList<Definition> definitions;

    public DefinitionList(){
        definitions = new ArrayList<>();
    }

    public void addDefinition(Definition definition){
        this.definitions.add(definition);
    }

    public void removeDefinition(Definition definition){
        this.definitions.remove(definition);
    }

    public ArrayList<Definition> getDefinitions(){
        return definitions;
    }

    public void clearList(){
        this.definitions.clear();
    }

    public boolean containsDefinition(String definitionString){
        boolean contains = false;

        for(Definition definition : definitions){
            if(definition.getDefinition().equals(definitionString)){
                contains = true;
            }
        }

        return  contains;
    }

    public void trimStrings(){
        for(Definition definition : definitions){
            if(definition.getDefinition() != null){
                definition.setDefinition(definition.getDefinition().trim());
            }

            if(definition.getSubject() != null){
                definition.setSubject(definition.getSubject().trim());
            }
        }
    }
}
