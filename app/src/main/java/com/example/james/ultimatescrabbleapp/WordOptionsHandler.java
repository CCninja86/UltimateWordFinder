package com.example.james.ultimatescrabbleapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.widget.Toast;

import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by james on 23/02/2017.
 */

public class WordOptionsHandler {

    private WordFinderSearchResultsFragment.OnFragmentInteractionListener wordFinderListener;
    private WordFinderDictionaryFragment.OnFragmentInteractionListener dictionaryListener;
    private Context context;
    private String word;
    private DefinitionList definitionList;
    private ArrayList<String> synonyms;
    private ProgressDialog progressDialog;
    private static final int MAX_ATTEMPTS = 5;

    public WordOptionsHandler(WordFinderSearchResultsFragment.OnFragmentInteractionListener wordFinderListener, WordFinderDictionaryFragment.OnFragmentInteractionListener dictionaryListener, Context context, String word){
        this.word = word;
        this.context = context;
        this.wordFinderListener = wordFinderListener;
        this.dictionaryListener = dictionaryListener;
        this.definitionList = new DefinitionList();
        this.synonyms = new ArrayList<>();
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void loadDefinitions(){
        GetDefinitionsTask getDefinitionsTask = new GetDefinitionsTask();
        getDefinitionsTask.execute();
    }

    public void loadSynonyms(){
        GetSynonymsTask getSynonymsTask = new GetSynonymsTask();
        getSynonymsTask.execute();
    }

    private class GetDefinitionsTask extends AsyncTask<Void, Void, Void> {

        public GetDefinitionsTask(){

        }

        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Searching...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String searchUrl;
            ArrayList<String> definitions = new ArrayList<>();
            ArrayList<String> subjectHeaders = new ArrayList<>();
            int numAttempts = 0;

            while (numAttempts < MAX_ATTEMPTS){
                try {
                    searchUrl = "http://dictionary.com/browse/" + word;
                    Document document = Jsoup.connect(searchUrl).userAgent("Mozilla/5.0 (Linux; Android 5.1.1; Vodafone Smart ultra 6"
                            + " Build/LMY47V) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.91"
                            + " Mobile Safari/537.36").referrer("http://www.google.com").get();


                    Elements definitionElements = document.getElementsByClass("def-content");


                    for(Element element : definitionElements){
                        Elements childElements = element.children();

                        for(Element child : childElements){
                            if(child.hasClass("dbox-italic")){
                                String sublistHeader = child.text();
                                subjectHeaders.add(sublistHeader);
                            }
                        }

                        String definition = element.text();

                        if(!definition.startsWith("see:")){
                            definitions.add(definition);
                        }
                    }

                    // Remove duplicates from sublist header array, and remove any false positives
                    Set<String> temp = new HashSet<>();
                    temp.addAll(subjectHeaders);
                    subjectHeaders.clear();
                    subjectHeaders.addAll(temp);

                    Iterator<String> iterator = subjectHeaders.iterator();

                    while(iterator.hasNext()){
                        String header = iterator.next();

                        if(!Character.isUpperCase(header.charAt(0))){
                            iterator.remove();
                        }
                    }

                    for(String definition : definitions){
                        for(String header : subjectHeaders){
                            if(definition.startsWith(header)){
                                String subject = definition.substring(0, definition.indexOf("."));
                                String[] definitionSublist = null;

                                if(!definition.contains("etc.")){
                                    definitionSublist = definition.split("\\.");
                                } else {
                                    StringBuilder stringBuilder = new StringBuilder(definition);

                                    for(int i = 0; i < definition.length(); i++){
                                        String character = String.valueOf(definition.charAt(i));

                                        if(character.equals(".")){
                                            String trailingSubstring = definition.substring(i - 3, i);

                                            if(trailingSubstring.equals("etc")){
                                                stringBuilder.setCharAt(i, ',');
                                                stringBuilder.setCharAt(i + 1, ' ');
                                            }
                                        }
                                    }

                                    definitionSublist = stringBuilder.toString().split("\\.");
                                }



                                for(String def : definitionSublist){
                                    if(!subjectHeaders.contains(def + ".")){
                                        Definition definitionObject = new Definition();
                                        definitionObject.setSubject(subject);
                                        definitionObject.setDefinition(def);

                                        if(!definitionList.containsDefinition(definition)){
                                            definitionList.addDefinition(definitionObject);
                                        }
                                    }
                                }

                            }
                        }

                        if(!definitionList.containsDefinition(definition)){
                            Definition definitionObject = new Definition();
                            definitionObject.setDefinition(definition);
                            definitionList.addDefinition(definitionObject);
                        }
                    }

                    Iterator<Definition> definitionIterator = definitionList.getDefinitions().iterator();

                    while (definitionIterator.hasNext()){
                        Definition definition = definitionIterator.next();

                        if(subjectHeaders.contains(definition.getDefinition().substring(0, definition.getDefinition().indexOf(".") + 1))){
                            definitionIterator.remove();
                        }
                    }

                    definitionList.trimStrings();

                    if(definitionList.getDefinitions().size() < 1){
                        numAttempts++;
                    } else {
                        break;
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    numAttempts++;
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            if(progressDialog != null && progressDialog.isShowing()){
                progressDialog.dismiss();
                progressDialog = null;
            }

            if(definitionList != null){
                if(definitionList.getDefinitions().size() > 0){
                    if(dictionaryListener != null){
                        dictionaryListener.onDictionaryFragmentInteraction(definitionList);
                    } else {
                        wordFinderListener.onResultsFragmentInteraction(definitionList);
                    }
                } else {
                    Toast.makeText(context, "No definitions found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "No definitions found", Toast.LENGTH_SHORT).show();
            }


        }
    }

    private class GetSynonymsTask extends AsyncTask<Object, Void, Void>{

        public GetSynonymsTask(){

        }

        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Searching...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Object... params) {
            try {
                if(word.contains(" ")){
                    word = word.toLowerCase().replaceAll(" ", "%20");
                }

                URL url = new URL("http://www.thesaurus.com/browse/" + word);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 5.1.1; Vodafone Smart ultra 6"
                        + " Build/LMY47V) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.91"
                        + " Mobile Safari/537.36");

                if(connection.getResponseCode() != 404){
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    ArrayList<String> entries = new ArrayList<>();

                    while((line = bufferedReader.readLine()) != null){
                        System.out.println(line);
                        if(line.contains("class=\"result synstart\"")){
                            String[] list = line.split("<b>Synonyms:</b>");


                            for(int i = 0; i < list.length; i++){
                                if(i > 0){
                                    String entry = list[i];
                                    String newEntry = entry.trim().substring(0, entry.indexOf("</div>") - 1);
                                    entries.add(newEntry);
                                }
                            }
                        }
                    }

                    for(String entry : entries){
                        String[] synonymList = entry.split(", ");

                        for(String synonym : synonymList){
                            synonyms.add(synonym);
                        }
                    }

                    // Remove any potential duplicate entries from ArrayList
                    Set<String> hashSet = new HashSet<>();
                    hashSet.addAll(synonyms);
                    synonyms.clear();
                    synonyms.addAll(hashSet);
                    hashSet = null;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            if(progressDialog != null && progressDialog.isShowing()){
                progressDialog.dismiss();
                progressDialog = null;
            }

            if(!synonyms.isEmpty() && synonyms != null){
                if(dictionaryListener != null){
                    dictionaryListener.onDictionaryFragmentInteraction(synonyms);
                } else {
                    wordFinderListener.onResultsFragmentInteraction(synonyms);
                }
            } else {
                Toast.makeText(context, "No synonyms found", Toast.LENGTH_SHORT).show();
            }

        }
    }
}